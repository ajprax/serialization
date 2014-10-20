package org.ajprax.serialization.schema.impl;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.ajprax.serialization.schema.RecordSchema;
import org.ajprax.serialization.schema.Schema;

public class RecordSchemaImpl extends AbstractSchema implements RecordSchema {

  public static RecordSchemaImpl create(
      final String name,
      final ImmutableMap<String, Schema> fieldSchemas
  ) {
    return new RecordSchemaImpl(name, Optional.of(fieldSchemas));
  }

  public static RecordSchemaImpl create(
      final String name
  ) {
    return new RecordSchemaImpl(name, Optional.empty());
  }

  private final String mName;
  private Optional<ImmutableMap<String, Schema>> mFieldSchemas;

  private RecordSchemaImpl(
      final String name,
      final Optional<ImmutableMap<String, Schema>> fieldSchemas
  ) {
    mName = name;
    mFieldSchemas = fieldSchemas;
  }

  void fillFieldSchemas(
      final ImmutableMap<String, Schema> fieldSchemas
  ) {
    mFieldSchemas = Optional.of(fieldSchemas);
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
  public RecordSchemaImpl asRecordSchema() {
    return this;
  }

  @Override
  public ImmutableMap<String, Schema> getFieldSchemas() {
    Preconditions.checkState(
        mFieldSchemas.isPresent(),
        "May not call getFieldSchemas on a partially built record Schema."
    );
    return mFieldSchemas.get();
  }
}
