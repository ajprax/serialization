package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.FixedSizeArraySchema;
import org.ajprax.serialization.schema.Schema;

public final class FixedSizeArraySchemaImpl extends AbstractSchema implements FixedSizeArraySchema {

  public static FixedSizeArraySchemaImpl create(
      final int size,
      final Schema elementSchema
  ) {
    return new FixedSizeArraySchemaImpl(size, elementSchema);
  }

  private final int mSize;
  private final Schema mElementSchema;

  private FixedSizeArraySchemaImpl(
      final int size,
      final Schema elementSchema
  ) {
    mSize = size;
    mElementSchema = elementSchema;
  }

  @Override
  public int getSize() {
    return mSize;
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }

  @Override
  public Type getType() {
    return Type.FIXED_SIZE_ARRAY;
  }

  @Override
  public String getName() {
    return String.format(
        "%dElementArray<%s>",
        mSize,
        mElementSchema.getName()
    );
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", getType())
        .add("size", getSize())
        .add("element_schema", getElementSchema())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getType(), getSize(), getElementSchema());
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof FixedSizeArraySchema)) {
      return false;
    } else {
      final FixedSizeArraySchema that = (FixedSizeArraySchema) obj;
      return Objects.equals(this.getType(), that.getType())
          && Objects.equals(this.getSize(), that.getSize())
          && this.getElementSchema().recursiveEquals(that.getElementSchema(), parentRecordNames);
    }
  }
}
