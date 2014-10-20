package org.ajprax.serialization.schema.impl;

import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.EnumSchema;

public final class EnumSchemaImpl extends AbstractSchema implements EnumSchema {

  public static EnumSchemaImpl create(
      final String name,
      final ImmutableSet<String> values
  ) {
    return new EnumSchemaImpl(name, values);
  }

  private final String mName;
  private final ImmutableSet<String> mValues;

  private EnumSchemaImpl(
      final String name,
      final ImmutableSet<String> values
  ) {
    mName = name;
    mValues = values;
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
  public EnumSchemaImpl asEnumSchema() {
    return this;
  }

  @Override
  public ImmutableSet<String> getValues() {
    return mValues;
  }
}
