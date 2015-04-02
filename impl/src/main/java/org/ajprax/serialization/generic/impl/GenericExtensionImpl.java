package org.ajprax.serialization.generic.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import org.ajprax.serialization.generic.GenericExtension;
import org.ajprax.serialization.schema.Schema;

public class GenericExtensionImpl<TAG> implements GenericExtension<TAG> {

  public static <T> GenericExtensionImpl<T> create(
      final Schema schema,
      final T tag,
      final byte[] value
  )  {
    return new GenericExtensionImpl<>(schema, tag, value);
  }

  private final Schema mSchema;
  private final TAG mTag;
  private final byte[] mValue;

  public GenericExtensionImpl(
      final Schema schema,
      final TAG tag,
      final byte[] value
  ) {
    mSchema = schema;
    mTag = tag;
    mValue = value;
  }

  @Override
  public TAG getTag() {
    return mTag;
  }

  @Override
  public Schema getSchema() {
    return mSchema;
  }

  @Override
  public byte[] getValue() {
    return mValue;
  }

  @Override
  public int hashCode() {
    return Objects.hash(mSchema, mTag, mValue);
  }

  @Override
  public boolean equals(
      final Object obj
  ) {
    // TODO consider if a generic and specific value with the same schema and value should be equal
    if (null == obj || !obj.getClass().equals(getClass())) {
      return false;
    } else {
      final GenericExtensionImpl<?> that = (GenericExtensionImpl<?>) obj;
      return Objects.equals(this.mSchema, that.mSchema)
          // TODO if recursive tags are allowed, do we need a recursion helper for checking equality?
          && Objects.equals(this.mTag, that.mTag)
          && Objects.equals(this.mValue, that.mValue);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("schema", mSchema)
        .add("tag", mTag)
        .add("value", mValue)
        .toString();
  }
}
