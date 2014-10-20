package org.ajprax.serialization.schema;

/**
 * Schema of Map data.
 *
 * Maps are variable size and all keys and values have the same Schemas.
 */
public interface MapSchema extends Schema {
  /**
   * @return The Schema of all keys in this Map.
   */
  Schema getKeySchema();

  /**
   * @return The Schema of all values in this Map.
   */
  Schema getValueSchema();
}
