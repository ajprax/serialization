package org.ajprax.serialization.schema;

/**
 * Schema of a fixed size Array.
 *
 * All elements of a fixed size array must have the same Schema.
 */
public interface FixedSizeArraySchema extends Schema {
  /**
   * @return The size of this Array.
   */
  public int getSize();

  /**
   * @return The Schema of all elements of this Array.
   */
  public Schema getElementSchema();
}
