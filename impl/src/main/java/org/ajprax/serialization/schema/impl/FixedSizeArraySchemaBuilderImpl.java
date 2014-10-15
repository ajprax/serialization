package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import org.ajprax.serialization.schema.FixedSizeArraySchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;

public class FixedSizeArraySchemaBuilderImpl implements SchemaBuilder.FixedSizeArraySchemaBuilder {

  public static FixedSizeArraySchemaBuilderImpl create() {
    return new FixedSizeArraySchemaBuilderImpl();
  }

  private Integer mSize;
  private Schema mElementSchema;

  private FixedSizeArraySchemaBuilderImpl() { }

  @Override
  public FixedSizeArraySchemaBuilderImpl setSize(
      final Integer size
  ) {
    Preconditions.checkState(
        mSize == null,
        "Size is already set to '%s'.",
        mSize
    );
    mSize = size;
    return this;
  }

  @Override
  public FixedSizeArraySchemaBuilderImpl setElementSchema(
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
  public Integer getSize() {
    return mSize;
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }

  @Override
  public FixedSizeArraySchema build() {
    Preconditions.checkState(
        mSize != null,
        "FixedSizeArraySchema may not be built with size unspecified."
    );
    Preconditions.checkState(
        mElementSchema != null,
        "FixedSizeArraySchema may not be built with element schema unspecified."
    );
    return FixedSizeArraySchemaImpl.create(mSize, mElementSchema);
  }
}
