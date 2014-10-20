package org.ajprax.serialization.schema;

/**
 * Schema of Array data.
 *
 * Arrays are variable length and all elements must have the same Schema.
 */
public interface ArraySchema extends Schema {
  /**
   * @return The Schema of all elements of this Array.
   */
  Schema getElementSchema();
}
