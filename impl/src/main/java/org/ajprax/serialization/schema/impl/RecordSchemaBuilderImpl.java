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

  public static RecordSchemaBuilderImpl create() {
    return new RecordSchemaBuilderImpl();
  }

  private String mName;
  private final Map<String, Schema> mFieldSchemas = Maps.newHashMap();
  private Optional<RecordSchemaImpl> mRecordSchema = Optional.empty();

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
  public RecordSchema getPlaceholderSchema() {
    Preconditions.checkState(
        mName != null,
        "May not create a placeholder Schema with name unset."
    );
    if (mRecordSchema.isPresent()) {
      return mRecordSchema.get();
    } else {
      mRecordSchema = Optional.of(RecordSchemaImpl.create(mName));
      return mRecordSchema.get();
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
    if (mRecordSchema.isPresent()) {
      mRecordSchema.get().fillFieldSchemas(fieldSchemas);
      return mRecordSchema.get();
    } else {
      return RecordSchemaImpl.create(mName, ImmutableMap.copyOf(mFieldSchemas));
    }
  }
}
