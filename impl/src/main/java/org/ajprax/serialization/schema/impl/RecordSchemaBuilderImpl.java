package org.ajprax.serialization.schema.impl;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.ajprax.serialization.schema.RecordSchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;

public class RecordSchemaBuilderImpl implements SchemaBuilder.RecordSchemaBuilder {

  public static final class PlaceholderSchema extends AbstractSchema implements RecordSchema {

    private final String mName;
    private ImmutableMap<String, Schema> mFieldSchemas;

    private PlaceholderSchema(
        final String name
    ) {
      mName = name;
    }

    private void fill(
        final ImmutableMap<String, Schema> fieldSchemas
    ) {
      mFieldSchemas = fieldSchemas;
    }

    @Override
    public Type getType() {
      return Type.RECORD;
    }

    @Override
    public String getName() {
      return mName;
    }

    @Override
    public ImmutableMap<String, Schema> getFieldSchemas() {
      Preconditions.checkState(
          mFieldSchemas != null,
          "May not call getFieldSchemas on a placeholder Schema until the corresponding Builder has been built."
      );
      return mFieldSchemas;
    }
  }

  public static RecordSchemaBuilderImpl create() {
    return new RecordSchemaBuilderImpl();
  }

  private String mName;
  private final Map<String, Schema> mFieldSchemas = Maps.newHashMap();
  private final List<PlaceholderSchema> mPlaceholderSchemas = Lists.newArrayList();

  @Override
  public RecordSchemaBuilderImpl setName(
      final String name
  ) {
    Preconditions.checkState(
        mName == null,
        "Name is already set to '%s'.",
        mName
    );
    mName = name;
    return this;
  }

  @Override
  public RecordSchemaBuilderImpl setFieldSchema(
      final String fieldName,
      final Schema fieldSchema
  ) {
    Preconditions.checkState(
        !mFieldSchemas.containsKey(fieldName),
        "Field named '%s' already set to schema '%s'.",
        fieldName,
        mFieldSchemas.get(fieldName)
    );
    mFieldSchemas.put(fieldName, fieldSchema);
    return this;
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public Schema getFieldSchema(
      final String fieldName
  ) {
    return mFieldSchemas.get(fieldName);
  }

  @Override
  public ImmutableMap<String, Schema> getFieldSchemas() {
    return ImmutableMap.copyOf(mFieldSchemas);
  }

  @Override
  public PlaceholderSchema getPlaceholderSchema() {
    Preconditions.checkState(
        mName != null,
        "May not create a placeholder Schema with name unset."
    );
    final PlaceholderSchema placeholder = new PlaceholderSchema(mName);
    mPlaceholderSchemas.add(placeholder);
    return placeholder;
  }

  @Override
  public RecordSchema build() {
    Preconditions.checkState(
        mName != null,
        "RecordSchema may not be built with name unspecified."
    );
    final ImmutableMap<String, Schema> fieldSchemas = ImmutableMap.copyOf(mFieldSchemas);
    // TODO validate that placeholder schemas do not create impossible to manifest records. Concretely, recursive fields must be inside of a variable size collection type (optional, union, set, map, array)
    mPlaceholderSchemas.forEach((PlaceholderSchema placeholder) -> placeholder.fill(fieldSchemas));
    return RecordSchemaImpl.create(mName, fieldSchemas);
  }
}