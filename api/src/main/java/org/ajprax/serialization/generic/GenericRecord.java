package org.ajprax.serialization.generic;

import com.google.common.collect.ImmutableMap;
import org.ajprax.serialization.schema.Schema;

public interface GenericRecord extends GenericValue<ImmutableMap<String, Object>> {
  default Schema getFieldSchema(String fieldName) {
    return getSchema().getFieldSchemas().get(fieldName);
  }

  @SuppressWarnings("unchecked")
  default <T> T get(String fieldName) {
    return (T) getValue().get(fieldName);
  }
}
