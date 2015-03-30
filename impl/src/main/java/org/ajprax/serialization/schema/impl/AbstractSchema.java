package org.ajprax.serialization.schema.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.Schema;

/**
 * Base class for Schemas.
 */
public abstract class AbstractSchema implements Schema {

  // Enum only
  public ImmutableSet<String> getEnumSymbols() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not an ENUM.", getName()));
  }

  // Extension only
  public Schema getTagSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not an EXTENSION.", getName()));
  }

  // FixedSizeArray only
  public int getSize() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a FIXED_SIZE_ARRAY.", getName()));
  }

  // Map only
  public Schema getKeySchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a MAP.", getName()));
  }

  // Map only
  public Schema getValueSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a MAP.", getName()));
  }

  // Union only
  public ImmutableList<Schema> getBranchSchemas() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a UNION.", getName()));
  }

  // Record only
  public ImmutableMap<String, Schema> getFieldSchemas() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a RECORD.", getName()));
  }

  // Array, FixedSizeArray, Set, Optional
  public Schema getElementSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not an ARRAY, FIXED_SIZE_ARRAY, SET, or OPTIONAL.", getName()));
  }

  @Override
  public String toString() {
    return SchemaRecursionHelpers.toString(this);
  }

  @Override
  public int hashCode() {
    return SchemaRecursionHelpers.hashCode(this);
  }

  @Override
  public boolean equals(
      final Object obj
  ) {
    if (obj == null || !(obj instanceof Schema)) {
      return false;
    } else {
      return SchemaRecursionHelpers.equals(this, (Schema) obj);
    }
  }
}
