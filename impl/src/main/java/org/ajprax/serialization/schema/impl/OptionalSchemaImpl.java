package org.ajprax.serialization.schema.impl;

import org.ajprax.serialization.schema.Schema;

public final class OptionalSchemaImpl extends AbstractSchema {

  public static OptionalSchemaImpl create(
      final Schema elementSchema
  ) {
    return new OptionalSchemaImpl(elementSchema);
  }

  private final Schema mElementSchema;

  private OptionalSchemaImpl(
      final Schema elementSchema
  ) {
    mElementSchema = elementSchema;
  }

  @Override
  public Type getType() {
    return Type.OPTIONAL;
  }

  @Override
  public String getName() {
    return String.format(
        "optional<%s>",
        mElementSchema.getName()
    );
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }
}
