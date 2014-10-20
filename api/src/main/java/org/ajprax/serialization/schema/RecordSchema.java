package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableMap;

/**
 * Schema of Record data.
 *
 * Records are named tuples, and their Schemas may be recursive.
 */
public interface RecordSchema extends Schema {
  /**
   * @return Map from field names to Schemas.
   */
  ImmutableMap<String, Schema> getFieldSchemas();
}
