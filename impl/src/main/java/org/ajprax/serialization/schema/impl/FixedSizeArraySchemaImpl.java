package org.ajprax.serialization.schema.impl;

import org.ajprax.serialization.schema.Schema;

public final class FixedSizeArraySchemaImpl extends AbstractSchema {

  public static FixedSizeArraySchemaImpl create(
      final int size,
      final Schema elementSchema
  ) {
    return new FixedSizeArraySchemaImpl(size, elementSchema);
  }

  private final int mSize;
  private final Schema mElementSchema;

  private FixedSizeArraySchemaImpl(
      final int size,
      final Schema elementSchema
  ) {
    mSize = size;
    mElementSchema = elementSchema;
  }

  @Override
  public Type getType() {
    return Type.FIXED_SIZE_ARRAY;
  }

  @Override
  public String getName() {
    return String.format(
        "%dElementArray<%s>",
        mSize,
        mElementSchema.getName()
    );
  }

  @Override
  public int getSize() {
    return mSize;
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }
}
