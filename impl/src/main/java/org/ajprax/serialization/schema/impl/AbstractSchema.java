package org.ajprax.serialization.schema.impl;

import com.google.common.collect.ImmutableSet;
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
  public boolean equals(
      final Object obj
  ) {
    return recursiveEquals(obj, ImmutableSet.of());
  }

  public abstract boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  );
}
