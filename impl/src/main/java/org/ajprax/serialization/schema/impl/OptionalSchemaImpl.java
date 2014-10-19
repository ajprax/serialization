package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.OptionalSchema;
import org.ajprax.serialization.schema.Schema;

public final class OptionalSchemaImpl extends AbstractSchema implements OptionalSchema {

  public static OptionalSchemaImpl create(
      final Schema elementSchema
  ) {
    return new OptionalSchemaImpl(elementSchema);
  }

  private final Schema mElementSchema;

  private OptionalSchemaImpl(
      final Schema elementSchema
  ) {
    mElementSchema = elementSchema;
  }

  @Override
  public Type getType() {
    return Type.OPTIONAL;
  }

  @Override
  public String getName() {
    return String.format(
        "optional<%s>",
        mElementSchema.getName()
    );
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }
}
