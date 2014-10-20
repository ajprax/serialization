package org.ajprax.serialization.schema.impl;

public final class PrimitiveSchemaImpl extends AbstractSchema {

  public static PrimitiveSchemaImpl create(
      Type type
  ) {
    return new PrimitiveSchemaImpl(type);
  }

  private final Type mType;

  private PrimitiveSchemaImpl(
      Type type
  ) {
    mType = type;
  }

  @Override
  public Type getType() {
    return mType;
  }

  @Override
  public String getName() {
    return mType.name();
  }
}
