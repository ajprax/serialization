package org.ajprax.serialization.schema.impl;

import org.ajprax.serialization.schema.Schema;

public final class SetSchemaImpl extends AbstractSchema {

  public static SetSchemaImpl create(
      final Schema elementSchema
  ) {
    return new SetSchemaImpl(elementSchema);
  }

  private final Schema mElementSchema;

  private SetSchemaImpl(
      final Schema elementSchema
  ) {
    mElementSchema = elementSchema;
  }

  @Override
  public Type getType() {
    return Type.SET;
  }

  @Override
  public String getName() {
    return String.format(
        "set<%s>",
        mElementSchema.getName()
    );
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }
}
