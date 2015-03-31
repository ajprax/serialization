package org.ajprax.serialization.generic.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import org.ajprax.serialization.generic.GenericUnion;
import org.ajprax.serialization.schema.Schema;

public class GenericUnionImpl implements GenericUnion {

  public static GenericUnionImpl create(
      final Schema schema,
      final int branchIndex,
      final Object value
  ) {
    return new GenericUnionImpl(schema, schema.getBranchSchemas().get(branchIndex), value);
  }

  private final Schema mSchema;
  private final Schema mBranchSchema;
  private final Object mValue;

  private GenericUnionImpl(
      final Schema schema,
      final Schema branchSchema,
      final Object value
  ) {
    mSchema = schema;
    mBranchSchema = branchSchema;
    mValue = value;
  }

  @Override
  public Schema getBranchSchema() {
    return mBranchSchema;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getTypedValue() {
    return (T) mValue;
  }

  @Override
  public Schema getSchema() {
    return mSchema;
  }

  @Override
  public Object getValue() {
    return mValue;
  }

  @Override
  public int hashCode() {
    return Objects.hash(mSchema, mBranchSchema, mValue);
  }

  @Override
  public boolean equals(
      final Object obj
  ) {
    // TODO consider if a generic and specific value with the same schema and value should be equal
    if (null == obj || !obj.getClass().equals(getClass())) {
      return false;
    } else {
      final GenericUnionImpl that = (GenericUnionImpl) obj;
      return Objects.equals(this.mSchema, that.mSchema)
          && Objects.equals(this.mBranchSchema, that.mBranchSchema)
          // TODO if recursive values are allowed, do we need a recursion helper for checking equality?
          && Objects.equals(this.mValue, that.mValue);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("schema", mSchema)
        .add("branch_schema", mBranchSchema)
        .add("value", mValue)
        .toString();
  }
}
