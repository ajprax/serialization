package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
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
  public String getName() {
    return mName;
  }

  @Override
  public Type getType() {
    return Type.ENUM;
  }

  @Override
  public ImmutableSet<String> getValues() {
    return mValues;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", getType())
        .add("name", getName())
        .add("values", getValues())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getType(), getName(), getValues());
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof EnumSchema)) {
      return false;
    } else {
      final EnumSchema that = (EnumSchema) obj;
      return Objects.equals(this.getType(), that.getType())
          && Objects.equals(this.getName(), that.getName())
          && Objects.equals(this.getValues(), that.getValues());
    }
  }
}
