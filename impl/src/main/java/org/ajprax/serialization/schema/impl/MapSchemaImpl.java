package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.MapSchema;
import org.ajprax.serialization.schema.Schema;

public final class MapSchemaImpl extends AbstractSchema implements MapSchema {

  public static MapSchemaImpl create(
      final Schema keySchema,
      final Schema valueSchema
  ) {
    return new MapSchemaImpl(keySchema, valueSchema);
  }

  private final Schema mKeySchema;
  private final Schema mValueSchema;

  public MapSchemaImpl(
      final Schema keySchema,
      final Schema valueSchema
  ) {
    mKeySchema = keySchema;
    mValueSchema = valueSchema;
  }

  @Override
  public Type getType() {
    return Type.MAP;
  }

  @Override
  public String getName() {
    return String.format(
        "map<%s, %s>",
        mKeySchema.getName(),
        mValueSchema.getName()
    );
  }

  @Override
  public Schema getKeySchema() {
    return mKeySchema;
  }

  @Override
  public Schema getValueSchema() {
    return mValueSchema;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", getType())
        .add("key_schema", getKeySchema())
        .add("value_schema", getValueSchema())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getType(), getKeySchema(), getValueSchema());
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof MapSchema)) {
      return false;
    } else {
      final MapSchema that = (MapSchema) obj;
      return Objects.equals(this.getType(), that.getType())
          && this.getKeySchema().recursiveEquals(that.getKeySchema(), parentRecordNames)
          && this.getValueSchema().recursiveEquals(that.getValueSchema(), parentRecordNames);
    }
  }
}
