package org.ajprax.serialization.schema;

public interface MapSchema extends Schema {
  Schema getKeySchema();
  Schema getValueSchema();
}
