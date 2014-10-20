package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableList;

// TODO do all branches have to be different?
/**
 * Schema for Union data.
 *
 * Union branches are ordered and must all have different Schemas.
 */
public interface UnionSchema extends Schema {
  /**
   * @return The ordered list of branch Schemas for this Union.
   */
  ImmutableList<Schema> getBranchSchemas();
}
