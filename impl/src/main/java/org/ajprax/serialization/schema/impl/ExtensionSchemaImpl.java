package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.ExtensionSchema;
import org.ajprax.serialization.schema.Schema;

public class ExtensionSchemaImpl extends AbstractSchema implements ExtensionSchema {

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
  public Schema getTagSchema() {
    return mTagSchema;
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
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", getType())
        .add("tag_schema", getTagSchema())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getType(), getTagSchema());
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof ExtensionSchema)) {
      return false;
    } else {
      final ExtensionSchema that = (ExtensionSchema) obj;
      return Objects.equals(this.getType(), that.getType())
          && this.getTagSchema().recursiveEquals(that.getTagSchema(), parentRecordNames);
    }
  }
}
