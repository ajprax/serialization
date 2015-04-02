package org.ajprax.serialization.schema;

import java.util.ServiceLoader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Super-interface of all Schema types.
 *
 * Provides specialization methods for dealing with non-primitive Schemas.
 */
public interface Schema {
  static SchemaBuilder.Provider PROVIDER = ServiceLoader.load(SchemaBuilder.Provider.class).iterator().next();

  static SchemaBuilder builder(Type type) {
    return PROVIDER.builder(type);
  }

  static Schema primitive(Type type) {
    return builder(type).build();
  }

  /** Schema types. */
  enum Type {
    UNSIGNED_8,
    UNSIGNED_16,
    UNSIGNED_32,
    UNSIGNED_64,
    UNSIGNED_BIG,
    SIGNED_8,
    SIGNED_16,
    SIGNED_32,
    SIGNED_64,
    SIGNED_BIG,
    FLOAT_32,
    FLOAT_64,
    FLOAT_BIG,
    BOOLEAN,
    STRING,
    ENUM,
    EXTENSION,
    ARRAY,
    FIXED_SIZE_ARRAY,
    SET,
    MAP,
    UNION,
    OPTIONAL,
    RECORD;
  }

  /**
   * @return The type of this Schema.
   */
  Type getType();

  /**
   * @return The name of this Schema. For primitive Schemas this is the name of the type.
   */
  String getName();

  // Enum only
  ImmutableSet<String> getEnumSymbols();

  // Extension only
  Schema getTagSchema();

  // FixedSizeArray only
  int getSize();

  // Map only
  Schema getKeySchema();

  // Map only
  Schema getValueSchema();

  // Union only
  ImmutableList<Schema> getBranchSchemas();

  // Record only
  ImmutableMap<String, Schema> getFieldSchemas();

  // Array, FixedSizeArray, Set, Optional
  Schema getElementSchema();
}
