package org.ajprax.serialization.generic.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import org.ajprax.serialization.generic.GenericValue;
import org.ajprax.serialization.schema.Schema;

public class GenericValueImpl<VALUE> implements GenericValue<VALUE> {

  public static <V> GenericValueImpl<V> create(
      final Schema schema,
      final V value
  ) {
    return new GenericValueImpl<>(schema, value);
  }

  private final Schema mSchema;
  private final VALUE mValue;

  private GenericValueImpl(
      final Schema schema,
      final VALUE value
  ) {
    mSchema = schema;
    mValue = value;
  }

  @Override
  public Schema getSchema() {
    return mSchema;
  }

  @Override
  public VALUE getValue() {
    return mValue;
  }

  @Override
  public int hashCode() {
    return Objects.hash(mSchema, mValue);
  }

  @Override
  public boolean equals(
      final Object obj
  ) {
    // TODO consider if a generic and specific value with the same schema and value should be equal
    if (null == obj || !obj.getClass().equals(getClass())) {
      return false;
    } else {
      final GenericValueImpl<?> that = (GenericValueImpl<?>) obj;
      return Objects.equals(this.mSchema, that.mSchema)
          // TODO if recursive values are allowed, do we need a recursion helper for checking equality?
          && Objects.equals(this.mValue, that.mValue);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("schema", mSchema)
        .add("value", mValue)
        .toString();
  }
}
