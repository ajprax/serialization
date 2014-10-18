package org.ajprax.serialization.schema.impl;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
  public ImmutableMap<String, Schema> getFieldSchemas() {
    return mFieldSchemas;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", getType())
        .add("name", getName())
        .add("field_schemas", getFieldSchemas())
        .toString();
  }

  @Override
  public int hashCode() {
    // TODO this will cause an infinite loop on recursive schemas.
    return Objects.hash(getType(), getName());
  }
}
