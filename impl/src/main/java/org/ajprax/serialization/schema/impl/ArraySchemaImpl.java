package org.ajprax.serialization.schema.impl;

import org.ajprax.serialization.schema.ArraySchema;
import org.ajprax.serialization.schema.Schema;

public final class ArraySchemaImpl extends AbstractSchema implements ArraySchema {

  public static ArraySchemaImpl create(
      final Schema elementSchema
  ) {
    return new ArraySchemaImpl(elementSchema);
  }

  private final Schema mElementSchema;

  private ArraySchemaImpl(
      final Schema elementSchema
  ) {
    mElementSchema = elementSchema;
  }

  @Override
  public Type getType() {
    return Type.ARRAY;
  }

  @Override
  public String getName() {
    return String.format(
        "array<%s>",
        mElementSchema.getName()
    );
  }

  @Override
  public ArraySchema asArraySchema() {
    return this;
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }
}
