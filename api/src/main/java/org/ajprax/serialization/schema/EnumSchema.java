package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableSet;

public interface EnumSchema extends Schema {
  ImmutableSet<String> getValues();
}
