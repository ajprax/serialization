package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Super-interface of all Schema builders.
 *
 * Provides specialization methods for building non-primitive Schemas.
 */
public interface SchemaBuilder {

  /**
   * @return The type of the Schema being built.
   */
  Schema.Type getType();

  // Array, FixedSizeArray, Set, Optional
  SchemaBuilder setElementSchema(Schema elementSchema);
  Schema getElementSchema();

  // FixedSizeArray
  SchemaBuilder setSize(Integer size);
  Integer getSize();

  // Enum
  SchemaBuilder setName(String name);
  SchemaBuilder setEnumSymbols(ImmutableSet<String> values);
  String getName();
  ImmutableSet<String> getEnumSymbols();

  // Extension
  SchemaBuilder setTagSchema(Schema tagSchema);
  Schema getTagSchema();

  // Map
  SchemaBuilder setKeySchema(Schema keySchema);
  SchemaBuilder setValueSchema(Schema valueSchema);
  Schema getKeySchema();
  Schema getValueSchema();

  // Record
  SchemaBuilder setFieldSchema(String fieldName, Schema fieldSchema);
  Schema getFieldSchema(String fieldName);
  ImmutableMap<String, Schema> getFieldSchemas();
  Schema getPlaceholderSchema();

  // Union
  SchemaBuilder addBranchSchema(Schema branchSchema);
  ImmutableList<Schema> getBranchSchemas();

  /**
   * @return The newly built Schema.
   */
  Schema build();

  /**
   * Provider interface which allows SchemaBuilder implementations to be created from the API package
   * via service loading.
   *
   * Users should not override this class.
   */
  interface Provider {
    SchemaBuilder builder(Schema.Type type);
  }
}
