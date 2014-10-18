package org.ajprax.serialization.schema.impl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
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

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(getClass())
          .add("type", getType().name())
          .add("name", getName())
          .add("field_schemas", (mFieldSchemas != null) ? mFieldSchemas : "Schema not yet built.")
          .toString();
    }

    @Override
    public int hashCode() {
      // TODO break this recursion.
      return Objects.hash(getType(), getName());
    }
  }

  public static RecordSchemaBuilderImpl create() {
    return new RecordSchemaBuilderImpl();
  }

  private String mName;
  private final Map<String, Schema> mFieldSchemas = Maps.newHashMap();
  private Optional<PlaceholderSchema> mPlaceholderSchema = Optional.empty();

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
    if (mPlaceholderSchema.isPresent()) {
      return mPlaceholderSchema.get();
    } else {
      mPlaceholderSchema = Optional.of(new PlaceholderSchema(mName));
      return mPlaceholderSchema.get();
    }
  }

  @Override
  public RecordSchema build() {
    Preconditions.checkState(
        mName != null,
        "RecordSchema may not be built with name unspecified."
    );
    final ImmutableMap<String, Schema> fieldSchemas = ImmutableMap.copyOf(mFieldSchemas);
    // TODO validate that placeholder schemas do not create impossible to manifest records. Concretely, recursive fields must be inside of a possibly 0 size collection type (optional, union, set, map, array)
    if (mPlaceholderSchema.isPresent()) {
      mPlaceholderSchema.get().fill(fieldSchemas);
    }
    return RecordSchemaImpl.create(mName, fieldSchemas);
  }
}
