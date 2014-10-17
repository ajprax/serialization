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

  static boolean recursiveEqualsImpl(
      final RecordSchema thiz,
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof RecordSchema)) {
      return false;
    } else {
      final RecordSchema that = (RecordSchema) obj;
      final boolean typesEqual = Objects.equals(thiz.getType(), that.getType());
      final boolean namesEqual = Objects.equals(thiz.getName(), that.getName());
      final boolean fieldNamesEqual = Objects.equals(
          thiz.getFieldSchemas().keySet(),
          that.getFieldSchemas().keySet()
      );
      if (typesEqual && namesEqual && fieldNamesEqual) {
        if (parentRecordNames.contains(thiz.getName())) {
          return true;
        } else {
          final ImmutableSet<String> augmentedRecursiveParentNames = ImmutableSet.<String>builder()
              .addAll(parentRecordNames)
              .add(thiz.getName())
              .build();
          boolean allFieldSchemasEqual = true;
          for (Map.Entry<String, Schema> fieldSchemaEntry : thiz.getFieldSchemas().entrySet()) {
            final String fieldName = fieldSchemaEntry.getKey();
            final Schema fieldSchema = fieldSchemaEntry.getValue();
            allFieldSchemasEqual = allFieldSchemasEqual || fieldSchema.recursiveEquals(
                that.getFieldSchemas().get(fieldName),
                augmentedRecursiveParentNames
            );
          }
          return allFieldSchemasEqual;
        }
      } else {
        return false;
      }
    }
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
    return Objects.hash(getType(), getName(), getFieldSchemas());
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> recursiveParentNames
  ) {
    return recursiveEqualsImpl(this, obj, recursiveParentNames);
  }
}
