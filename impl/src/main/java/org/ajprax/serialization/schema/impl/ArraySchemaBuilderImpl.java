package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import org.ajprax.serialization.schema.ArraySchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;

public class ArraySchemaBuilderImpl implements SchemaBuilder.ArraySchemaBuilder {

  public static ArraySchemaBuilderImpl create() {
    return new ArraySchemaBuilderImpl();
  }

  private Schema mElementSchema;

  private ArraySchemaBuilderImpl() { }

  @Override
  public ArraySchemaBuilderImpl setElementSchema(
      final Schema elementSchema
  ) {
    Preconditions.checkState(
        mElementSchema == null,
        "Element schema already set to '%s'",
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
  public ArraySchema build() {
    Preconditions.checkState(
        mElementSchema != null,
        "ArraySchema may not be built with element schema unspecified."
    );
    return ArraySchemaImpl.create(mElementSchema);
  }
}
