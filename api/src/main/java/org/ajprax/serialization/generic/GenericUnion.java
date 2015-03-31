package org.ajprax.serialization.generic;

import org.ajprax.serialization.schema.Schema;

public interface GenericUnion extends GenericValue<Object> {
  Schema getBranchSchema();

  <T> T getTypedValue();
}
