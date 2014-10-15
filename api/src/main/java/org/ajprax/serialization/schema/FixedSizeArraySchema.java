package org.ajprax.serialization.schema;

public interface FixedSizeArraySchema extends Schema {
  public int getSize();
  public Schema getElementSchema();
}
