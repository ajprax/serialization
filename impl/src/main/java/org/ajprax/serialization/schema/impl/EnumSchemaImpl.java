package org.ajprax.serialization.schema.impl;

public class EnumSchemaImpl extends AbstractSchema {

  public static EnumSchemaImpl create(
      final String name
  ) {
    return new EnumSchemaImpl(name);
  }

  private final String mName;

  private EnumSchemaImpl(
      final String name
  ) {
    mName = name;
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public Type getType() {
    return Type.ENUM;
  }
}
