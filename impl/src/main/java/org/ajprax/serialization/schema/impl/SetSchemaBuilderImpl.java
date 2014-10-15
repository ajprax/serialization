package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;
import org.ajprax.serialization.schema.SetSchema;

public class SetSchemaBuilderImpl implements SchemaBuilder.SetSchemaBuilder {

  public static SetSchemaBuilderImpl create() {
    return new SetSchemaBuilderImpl();
  }

  private Schema mElementSchema;

  private SetSchemaBuilderImpl() { }

  @Override
  public SetSchemaBuilderImpl setElementSchema(
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
  public SetSchema build() {
    Preconditions.checkState(
        mElementSchema != null,
        "SetSchema may not be built with element schema unspecified."
    );
    return SetSchemaImpl.create(mElementSchema);
  }
}
