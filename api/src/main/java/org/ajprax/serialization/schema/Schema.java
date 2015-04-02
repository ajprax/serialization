package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.impl.SchemaBuilderFactory;

/**
 * Super-interface of all Schema types.
 *
 * Provides specialization methods for dealing with non-primitive Schemas.
 */
public interface Schema {
  interface Builder {
    Type getType();

    // Array, FixedSizeArray, Set, Optional
    Builder setElementSchema(Schema elementSchema);
    Schema getElementSchema();

    // FixedSizeArray
    Builder setSize(Integer size);
    Integer getSize();

    // Enum
    Builder setName(String name);
    Builder setEnumSymbols(ImmutableSet<String> values);
    String getName();
    ImmutableSet<String> getEnumSymbols();

    // Extension
    Builder setTagSchema(Schema tagSchema);
    Schema getTagSchema();

    // Map
    Builder setKeySchema(Schema keySchema);
    Builder setValueSchema(Schema valueSchema);
    Schema getKeySchema();
    Schema getValueSchema();

    // Record
    Builder setFieldSchema(String fieldName, Schema fieldSchema);
    Schema getFieldSchema(String fieldName);
    ImmutableMap<String, Schema> getFieldSchemas();
    Schema getPlaceholderSchema();

    // Union
    Builder addBranchSchema(Schema branchSchema);
    ImmutableList<Schema> getBranchSchemas();

    Schema build();
  }

  static Builder builder(Type type) {
    return SchemaBuilderFactory.INSTANCE.builder(type);
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
