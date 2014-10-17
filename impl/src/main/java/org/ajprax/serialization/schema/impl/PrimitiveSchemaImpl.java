package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.Schema;

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

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", getType())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getType());
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof Schema)) {
      return false;
    } else {
      final Schema that = (Schema) obj;
      return Objects.equals(this.getType(), that.getType());
    }
  }
}
