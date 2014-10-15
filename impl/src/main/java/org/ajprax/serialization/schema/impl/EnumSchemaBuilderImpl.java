package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.EnumSchema;
import org.ajprax.serialization.schema.SchemaBuilder;

public class EnumSchemaBuilderImpl implements SchemaBuilder.EnumSchemaBuilder {

  public static EnumSchemaBuilderImpl create() {
    return new EnumSchemaBuilderImpl();
  }

  private String mName;
  private ImmutableSet<String> mValues;

  @Override
  public EnumSchemaBuilderImpl setName(
      final String name
  ) {
    Preconditions.checkState(
        mName == null,
        "Name is already set to '%s'.",
        mName
    );
    mName = name;
    return this;
  }

  @Override
  public EnumSchemaBuilderImpl setValues(
      final ImmutableSet<String> values
  ) {
    // TODO validate that values are valid enum names. Consider how this works across languages.
    Preconditions.checkState(
        mValues == null,
        "Values are already set to '%s'.",
        mValues
    );
    mValues = values;
    return this;
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public ImmutableSet<String> getValues() {
    return mValues;
  }

  @Override
  public EnumSchema build() {
    Preconditions.checkState(
        mName != null,
        "EnumSchema may not be built with name unspecified"
    );
    Preconditions.checkState(
        mValues != null,
        "EnumSchema may not be built with values unspecified."
    );
    return EnumSchemaImpl.create(mName, mValues);
  }
}
