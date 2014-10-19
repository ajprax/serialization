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

public abstract class AbstractSchema implements Schema {
  @Override
  public EnumSchema asEnumSchema() {
    return (EnumSchema) this;
  }
  @Override
  public ExtensionSchema asExtensionSchema() {
    return (ExtensionSchema) this;
  }

  @Override
  public ArraySchema asArraySchema() {
    return (ArraySchema) this;
  }

  @Override
  public FixedSizeArraySchema asFixedSizeArraySchema() {
    return (FixedSizeArraySchema) this;
  }

  @Override
  public SetSchema asSetSchema() {
    return (SetSchema) this;
  }

  @Override
  public MapSchema asMapSchema() {
    return (MapSchema) this;
  }

  @Override
  public UnionSchema asUnionSchema() {
    return (UnionSchema) this;
  }

  @Override
  public OptionalSchema asOptionalSchema() {
    return (OptionalSchema) this;
  }

  @Override
  public RecordSchema asRecordSchema() {
    return (RecordSchema) this;
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
      final Schema that = (Schema) obj;
      return SchemaRecursionHelpers.equals(this, that);
    }
  }
}
