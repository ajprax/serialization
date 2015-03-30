package org.ajprax.serialization.schema.impl;

import org.ajprax.serialization.schema.ArraySchema;
import org.ajprax.serialization.schema.EnumSchema;
import org.ajprax.serialization.schema.ExtensionSchema;
import org.ajprax.serialization.schema.FixedSizeArraySchema;
import org.ajprax.serialization.schema.MapSchema;
import org.ajprax.serialization.schema.OptionalSchema;
import org.ajprax.serialization.schema.RecordSchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SetSchema;
import org.ajprax.serialization.schema.UnionSchema;

/**
 * Base class for Schemas.
 */
public abstract class AbstractSchema implements Schema {
  @Override
  public EnumSchema asEnumSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not an EnumSchema.", getName()));
  }

  @Override
  public ExtensionSchema asExtensionSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not an ExtensionSchema.", getName()));
  }

  @Override
  public ArraySchema asArraySchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not an ArraySchema.", getName()));
  }

  @Override
  public FixedSizeArraySchema asFixedSizeArraySchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a FixedSizeArraySchema.", getName()));
  }

  @Override
  public SetSchema asSetSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a SetSchema.", getName()));
  }

  @Override
  public MapSchema asMapSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a MapSchema.", getName()));
  }

  @Override
  public UnionSchema asUnionSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a UnionSchema.", getName()));
  }

  @Override
  public OptionalSchema asOptionalSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not an OptionalSchema.", getName()));
  }

  @Override
  public RecordSchema asRecordSchema() {
    throw new UnsupportedOperationException(String.format("Schema: '%s' is not a RecordSchema.", getName()));
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
