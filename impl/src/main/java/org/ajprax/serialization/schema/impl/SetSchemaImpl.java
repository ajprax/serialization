package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.OptionalSchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SetSchema;

public final class SetSchemaImpl extends AbstractSchema implements SetSchema {

  public static SetSchemaImpl create(
      final Schema elementSchema
  ) {
    return new SetSchemaImpl(elementSchema);
  }

  private final Schema mElementSchema;

  private SetSchemaImpl(
      final Schema elementSchema
  ) {
    mElementSchema = elementSchema;
  }

  @Override
  public Type getType() {
    return Type.SET;
  }

  @Override
  public String getName() {
    return String.format(
        "set<%s>",
        mElementSchema.getName()
    );
  }

  @Override
  public Schema getElementSchema() {
    return mElementSchema;
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof SetSchema)) {
      return false;
    } else {
      final SetSchema that = (SetSchema) obj;
      return Objects.equals(this.getType(), that.getType())
          && this.getElementSchema().recursiveEquals(that.getElementSchema(), parentRecordNames);
    }
  }
}
