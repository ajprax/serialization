package org.ajprax.serialization.schema;

/**
 * Schema of optional data.
 *
 * Optional data is equivalent to Array data with exactly zero or one element.
 */
public interface OptionalSchema extends Schema {
  /**
   * @return The Schema of the optional element.
   */
  Schema getElementSchema();
}
