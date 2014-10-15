package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import org.ajprax.serialization.schema.MapSchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;

public class MapSchemaBuilderImpl implements SchemaBuilder.MapSchemaBuilder {

  public static MapSchemaBuilderImpl create() {
    return new MapSchemaBuilderImpl();
  }

  private Schema mKeySchema;
  private Schema mValueSchema;

  private MapSchemaBuilderImpl() { }

  @Override
  public MapSchemaBuilderImpl setKeySchema(
      final Schema keySchema
  ) {
    Preconditions.checkState(
        mKeySchema == null,
        "Key schema is already set to '%s'.",
        mKeySchema
    );
    mKeySchema = keySchema;
    return this;
  }

  @Override
  public MapSchemaBuilderImpl setValueSchema(
      final Schema valueSchema
  ) {
    Preconditions.checkState(
        mValueSchema == null,
        "Value schema is already set to '%s'.",
        mValueSchema
    );
    mValueSchema = valueSchema;
    return this;
  }

  @Override
  public Schema getKeySchema() {
    return mKeySchema;
  }

  @Override
  public Schema getValueSchema() {
    return mValueSchema;
  }

  @Override
  public MapSchema build() {
    Preconditions.checkState(
        mKeySchema != null,
        "MapSchema may not be built with key schema unspecified."
    );
    Preconditions.checkState(
        mValueSchema != null,
        "MapSchema may not be built with value schema unspecified."
    );
    return MapSchemaImpl.create(mKeySchema, mValueSchema);
  }
}
