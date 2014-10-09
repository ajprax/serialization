package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableMap;

public interface RecordSchema extends Schema {
  ImmutableMap<String, Schema> getFieldSchemas();
}
