package org.ajprax.serialization.schema;

/**
 * Schema of data of an unknown type.
 *
 * Extensions carry a descriptor tag, which instructs a decoder on how to read the data at runtime.
 */
public interface ExtensionSchema extends Schema {
  /**
   * @return The Schema of the descriptor tag. This is not the Schema of the data contained in the
   *     extension.
   */
  Schema getTagSchema();
}
