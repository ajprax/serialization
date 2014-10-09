package org.ajprax.serialization.schema;

public interface ArraySchema extends Schema {
  Schema getElementSchema();
}
