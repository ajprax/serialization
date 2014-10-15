package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import org.ajprax.serialization.schema.OptionalSchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;

public class OptionalSchemaBuilderImpl implements SchemaBuilder.OptionalSchemaBuilder {

  public static OptionalSchemaBuilderImpl create() {
    return new OptionalSchemaBuilderImpl();
  }

  private Schema mElementSchema;

  private OptionalSchemaBuilderImpl() { }

  @Override
  public OptionalSchemaBuilderImpl setElementSchema(
      final Schema elementSchema
  ) {
    Preconditions.checkState(
        mElementSchema == null,
        "Element schema is already set to '%s'.",
        mElementSchema
    );
    mElementSchema = elementSchema;
    return this;
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }

  @Override
  public OptionalSchema build() {
    Preconditions.checkState(
        mElementSchema != null,
        "OptionalSchema may not be built with element schema unspecified."
    );
    return OptionalSchemaImpl.create(mElementSchema);
  }
}
