package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableList;

public interface UnionSchema extends Schema {
  ImmutableList<Schema> getBranchSchemas();
}
