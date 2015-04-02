package org.ajprax.serialization.generic.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import org.ajprax.serialization.generic.GenericRecord;
import org.ajprax.serialization.schema.Schema;

public class GenericRecordImpl implements GenericRecord {

  public static GenericRecordImpl create(
      final Schema schema,
      final ImmutableMap<String, Object> value
  ) {
    return new GenericRecordImpl(schema, value);
  }

  private final Schema mSchema;
  private final ImmutableMap<String, Object> mValue;

  private GenericRecordImpl(
      final Schema schema,
      final ImmutableMap<String, Object> value
  ) {
    mSchema = schema;
    mValue = value;
    // TODO ensure that values match the schema, do necessary number conversion?
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(final String fieldName) {
    return (T) mValue.get(fieldName);
  }

  @Override
  public Schema getSchema() {
    return mSchema;
  }

  @Override
  public ImmutableMap<String, Object> getValue() {
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
      final GenericRecordImpl that = (GenericRecordImpl) obj;
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
