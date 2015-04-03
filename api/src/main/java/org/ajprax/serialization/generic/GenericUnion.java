package org.ajprax.serialization.generic;

import org.ajprax.serialization.schema.Schema;

public interface GenericUnion extends GenericValue<Object> {
  int getBranchIndex();

  default Schema getBranchSchema() {
    return getSchema().getBranchSchemas().get(getBranchIndex());
  }

  @SuppressWarnings("unchecked")
  default <T> T getTypedValue() {
    return (T) getValue();
  }
}
