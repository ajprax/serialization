package org.ajprax.serialization.schema;

/**
 * Schema for Set data.
 *
 * Sets are variable size, contain each element at most one time, and all elements must have the
 * same Schema.
 */
public interface SetSchema extends Schema {
  /**
   * @return The Schema of all elements of this Set.
   */
  Schema getElementSchema();
}
