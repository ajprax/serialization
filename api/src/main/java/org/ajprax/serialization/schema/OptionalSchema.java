package org.ajprax.serialization.schema;

public interface OptionalSchema extends Schema {
  Schema getElementSchema();
}
