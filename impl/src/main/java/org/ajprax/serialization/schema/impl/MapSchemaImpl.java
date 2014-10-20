package org.ajprax.serialization.schema.impl;

import org.ajprax.serialization.schema.MapSchema;
import org.ajprax.serialization.schema.Schema;

public final class MapSchemaImpl extends AbstractSchema implements MapSchema {

  public static MapSchemaImpl create(
      final Schema keySchema,
      final Schema valueSchema
  ) {
    return new MapSchemaImpl(keySchema, valueSchema);
  }

  private final Schema mKeySchema;
  private final Schema mValueSchema;

  public MapSchemaImpl(
      final Schema keySchema,
      final Schema valueSchema
  ) {
    mKeySchema = keySchema;
    mValueSchema = valueSchema;
  }

  @Override
  public Type getType() {
    return Type.MAP;
  }

  @Override
  public String getName() {
    return String.format(
        "map<%s, %s>",
        mKeySchema.getName(),
        mValueSchema.getName()
    );
  }

  @Override
  public MapSchemaImpl asMapSchema() {
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
}
