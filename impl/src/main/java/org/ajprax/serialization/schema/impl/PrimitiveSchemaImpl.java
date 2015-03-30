package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public final class PrimitiveSchemaImpl extends AbstractSchema {

  private static final ImmutableMap<Type, PrimitiveSchemaImpl> INSTANCES = ImmutableMap
      .<Type, PrimitiveSchemaImpl>builder()
      .put(Type.UNSIGNED_8, new PrimitiveSchemaImpl(Type.UNSIGNED_8))
      .put(Type.UNSIGNED_16, new PrimitiveSchemaImpl(Type.UNSIGNED_16))
      .put(Type.UNSIGNED_32, new PrimitiveSchemaImpl(Type.UNSIGNED_32))
      .put(Type.UNSIGNED_64, new PrimitiveSchemaImpl(Type.UNSIGNED_64))
      .put(Type.UNSIGNED_BIG, new PrimitiveSchemaImpl(Type.UNSIGNED_BIG))
      .put(Type.SIGNED_8, new PrimitiveSchemaImpl(Type.SIGNED_8))
      .put(Type.SIGNED_16, new PrimitiveSchemaImpl(Type.SIGNED_16))
      .put(Type.SIGNED_32, new PrimitiveSchemaImpl(Type.SIGNED_32))
      .put(Type.SIGNED_64, new PrimitiveSchemaImpl(Type.SIGNED_64))
      .put(Type.SIGNED_BIG, new PrimitiveSchemaImpl(Type.SIGNED_BIG))
      .put(Type.FLOAT_32, new PrimitiveSchemaImpl(Type.FLOAT_32))
      .put(Type.FLOAT_64, new PrimitiveSchemaImpl(Type.FLOAT_64))
      .put(Type.FLOAT_BIG, new PrimitiveSchemaImpl(Type.FLOAT_BIG))
      .put(Type.BOOLEAN, new PrimitiveSchemaImpl(Type.BOOLEAN))
      .put(Type.STRING, new PrimitiveSchemaImpl(Type.STRING))
      .build();

  public static PrimitiveSchemaImpl create(
      Type type
  ) {
    Preconditions.checkArgument(
        INSTANCES.containsKey(type),
        "Schema type: '%s' is not a primitive type.",
        type
    );
    return INSTANCES.get(type);
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
