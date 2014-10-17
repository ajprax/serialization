package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.ArraySchema;
import org.ajprax.serialization.schema.Schema;

public final class ArraySchemaImpl extends AbstractSchema implements ArraySchema {

  public static ArraySchemaImpl create(
      final Schema elementSchema
  ) {
    return new ArraySchemaImpl(elementSchema);
  }

  private final Schema mElementSchema;

  private ArraySchemaImpl(
      final Schema elementSchema
  ) {
    mElementSchema = elementSchema;
  }

  @Override
  public Type getType() {
    return Type.ARRAY;
  }

  @Override
  public String getName() {
    return String.format(
        "array<%s>",
        mElementSchema.getName()
    );
  }

  @Override
  public ArraySchema asArraySchema() {
    return this;
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", getType().name())
        .add("element_schema", getElementSchema().toString())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getType(), getElementSchema());
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof ArraySchema)) {
      return false;
    } else {
      final ArraySchema that = (ArraySchema) obj;
      return Objects.equals(this.getType(), that.getType())
          && this.getElementSchema().recursiveEquals(that.getElementSchema(), parentRecordNames);
    }
  }
}
