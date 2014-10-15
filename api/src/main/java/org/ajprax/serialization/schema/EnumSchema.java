package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableSet;

public interface EnumSchema {
  ImmutableSet<String> getValues();
}
