package org.ajprax.serialization.schema;

public interface ExtensionSchema extends Schema {
  Schema getTagSchema();
}
