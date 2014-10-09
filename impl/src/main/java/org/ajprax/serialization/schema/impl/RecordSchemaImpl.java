package org.ajprax.serialization.schema.impl;

import com.google.common.collect.ImmutableMap;
import org.ajprax.serialization.schema.RecordSchema;
import org.ajprax.serialization.schema.Schema;

public class RecordSchemaImpl extends AbstractSchema implements RecordSchema {

  public static RecordSchemaImpl create(
      final String name,
      final ImmutableMap<String, Schema> fieldSchemas
  ) {
    return new RecordSchemaImpl(name, fieldSchemas);
  }

  private final String mName;
  private final ImmutableMap<String, Schema> mFieldSchemas;

  private RecordSchemaImpl(
      final String name,
      final ImmutableMap<String, Schema> fieldSchemas
  ) {
    mName = name;
    mFieldSchemas = fieldSchemas;
  }

  @Override
  public Type getType() {
    return Type.RECORD;
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public RecordSchema asRecordSchema() {
    return this;
  }

  @Override
  public ImmutableMap<String, Schema> getFieldSchemas() {
    return mFieldSchemas;
  }
}
