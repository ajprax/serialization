package org.ajprax.serialization.schema.impl;

import com.google.common.collect.ImmutableSet;

public final class EnumSchemaImpl extends AbstractSchema {

  public static EnumSchemaImpl create(
      final String name,
      final ImmutableSet<String> values
  ) {
    return new EnumSchemaImpl(name, values);
  }

  private final String mName;
  private final ImmutableSet<String> mEnumSymbols;

  private EnumSchemaImpl(
      final String name,
      final ImmutableSet<String> enumSymbols
  ) {
    mName = name;
    mEnumSymbols = enumSymbols;
  }

  @Override
  public Type getType() {
    return Type.ENUM;
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public ImmutableSet<String> getEnumSymbols() {
    return mEnumSymbols;
  }
}
