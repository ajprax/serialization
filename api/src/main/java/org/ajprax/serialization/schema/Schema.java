package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableSet;

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

  /**
   * @return This Schema as an EnumSchema if its Type is {@link Type#ENUM}.
   */
  EnumSchema asEnumSchema();

  /**
   * @return This Schema as an ExtensionSchema if its Type is {@link Type#EXTENSION}.
   */
  ExtensionSchema asExtensionSchema();

  /**
   * @return This Schema as an ArraySchema if its Type is {@link Type#ARRAY}.
   */
  ArraySchema asArraySchema();

  /**
   * @return This Schema as a FixedSizeArraySchema if its Type is {@link Type#FIXED_SIZE_ARRAY}.
   */
  FixedSizeArraySchema asFixedSizeArraySchema();

  /**
   * @return This Schema as a SetSchema if its Type is {@link Type#SET}.
   */
  SetSchema asSetSchema();

  /**
   * @return This Schema as a MapSchema if its type is {@link Type#MAP}.
   */
  MapSchema asMapSchema();

  /**
   * @return This Schema as a UnionSchema if its type is {@link Type#UNION}.
   */
  UnionSchema asUnionSchema();

  /**
   * @return This Schema as an OptionalSchema if its type is {@link Type#OPTIONAL}.
   */
  OptionalSchema asOptionalSchema();

  /**
   * @return This Schema as a RecordSchema if its type is {@link Type#RECORD}.
   */
  RecordSchema asRecordSchema();
}
