package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableSet;

// TODO consider how these rules should look for other languages. Should the rules be universal?
/**
 * Schema of enumerated data.
 *
 * Enum Schema names and values may contain only alphanumeric characters, underscore (_), and
 * dollar ($), and may not begin with a number.
 */
public interface EnumSchema extends Schema {
  /**
   * @return The set of enumerated values of this Enum.
   */
  ImmutableSet<String> getValues();
}
