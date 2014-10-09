package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface Schema {
  /** Schema types. */
  public enum Type {
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
    EXTENSION,
    ENUM,
    ARRAY,
    SET,
    MAP,
    UNION,
    OPTIONAL,
    RECORD;
  }

  public static interface Builder {
    Type getType();
    Builder withName(String name);
    String getName();
    Builder withElementSchema(Schema elementSchema);
    Schema getElementSchema();
    Builder withKeySchema(Schema keySchema);
    Schema getKeySchema();
    Builder withValueSchema(Schema valueSchema);
    Schema getValueSchema();
    Builder withBranchSchemas(ImmutableList<Schema> branchSchemas);
    ImmutableList<Schema> getBranchSchemas();
    Builder withFieldSchemas(ImmutableMap<String, Schema> fieldSchemas);
    ImmutableMap<String, Schema> getFieldSchemas();
    Schema build();
  }

  /**
   * @return The type of this Schema.
   */
  Type getType();

  /**
   * @return The name of this Schema. For primitive Schemas this is the name of the type.
   */
  String getName();

  /**
   * @return This Schema as an ArraySchema if its Type is {@link Type#ARRAY}.
   * @throws org.ajprax.serialization.errors.TypeMismatchException if this Schema is not an Array.
   */
  ArraySchema asArraySchema();

  /**
   * @return This Schema as a SetSchema if its Type is {@link Type#SET}
   * @throws org.ajprax.serialization.errors.TypeMismatchException if this Schema is not a Set.
   */
  SetSchema asSetSchema();

  /**
   * @return This Schema as a MapSchema if its type is {@link Type#MAP}
   * @throws org.ajprax.serialization.errors.TypeMismatchException if this Schema is not a Map.
   */
  MapSchema asMapSchema();

  /**
   * @return This Schema as a UnionSchema if its type is {@link Type#UNION}
   * @throws org.ajprax.serialization.errors.TypeMismatchException if this Schema is not a Union.
   */
  UnionSchema asUnionSchema();

  /**
   * @return This Schema as an OptionalSchema if its type is {@link Type#OPTIONAL}
   * @throws org.ajprax.serialization.errors.TypeMismatchException if this Schema is not Optional.
   */
  OptionalSchema asOptionalSchema();

  /**
   * @return This Schema as a RecordSchema if its type is {@link Type#RECORD}
   * @throws org.ajprax.serialization.errors.TypeMismatchException if this Schema is not an Record.
   */
  RecordSchema asRecordSchema();
}
