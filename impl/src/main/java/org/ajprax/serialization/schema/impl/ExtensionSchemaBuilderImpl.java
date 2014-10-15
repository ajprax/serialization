package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import org.ajprax.serialization.schema.ExtensionSchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;

public class ExtensionSchemaBuilderImpl implements SchemaBuilder.ExtensionSchemaBuilder {

  public static ExtensionSchemaBuilderImpl create() {
    return new ExtensionSchemaBuilderImpl();
  }

  private Schema mTagSchema;

  private ExtensionSchemaBuilderImpl() { }

  @Override
  public SchemaBuilder.ExtensionSchemaBuilder setTagSchema(
      final Schema tagSchema
  ) {
    Preconditions.checkState(
        mTagSchema == null,
        "Tag schema is already set to '%s'.",
        mTagSchema
    );
    mTagSchema = tagSchema;
    return this;
  }

  @Override
  public Schema getTagSchema() {
    return mTagSchema;
  }

  @Override
  public ExtensionSchema build() {
    Preconditions.checkState(
        mTagSchema != null,
        "ExtensionSchema may not be built with tag schema unspecified."
    );
    return ExtensionSchemaImpl.create(mTagSchema);
  }
}
