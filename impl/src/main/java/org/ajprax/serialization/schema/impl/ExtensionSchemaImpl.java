package org.ajprax.serialization.schema.impl;

import org.ajprax.serialization.schema.Schema;

public class ExtensionSchemaImpl extends AbstractSchema {

  public static ExtensionSchemaImpl create(
      final Schema tagSchema
  ) {
    return new ExtensionSchemaImpl(tagSchema);
  }

  private final Schema mTagSchema;

  private ExtensionSchemaImpl(
      final Schema tagSchema
  ) {
    mTagSchema = tagSchema;
  }

  @Override
  public Type getType() {
    return Type.EXTENSION;
  }

  @Override
  public String getName() {
    return String.format(
        "extension<%s>",
        mTagSchema.getName()
    );
  }

  @Override
  public Schema getTagSchema() {
    return mTagSchema;
  }
}
