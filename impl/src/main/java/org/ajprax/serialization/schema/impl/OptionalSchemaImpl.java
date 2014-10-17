package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.OptionalSchema;
import org.ajprax.serialization.schema.Schema;

public final class OptionalSchemaImpl extends AbstractSchema implements OptionalSchema {

  public static OptionalSchemaImpl create(
      final Schema elementSchema
  ) {
    return new OptionalSchemaImpl(elementSchema);
  }

  private final Schema mElementSchema;

  private OptionalSchemaImpl(
      final Schema elementSchema
  ) {
    mElementSchema = elementSchema;
  }

  @Override
  public Type getType() {
    return Type.OPTIONAL;
  }

  @Override
  public String getName() {
    return String.format(
        "optional<%s>",
        mElementSchema.getName()
    );
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", getType())
        .add("element_schema", getElementSchema())
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
    if (obj == null || !(obj instanceof OptionalSchema)) {
      return false;
    } else {
      final OptionalSchema that = (OptionalSchema) obj;
      return Objects.equals(this.getType(), that.getType())
          && this.getElementSchema().recursiveEquals(that.getElementSchema(), parentRecordNames);
    }
  }
}
