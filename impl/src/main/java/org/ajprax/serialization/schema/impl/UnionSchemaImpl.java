package org.ajprax.serialization.schema.impl;

import com.google.common.collect.ImmutableList;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.UnionSchema;

public class UnionSchemaImpl extends AbstractSchema implements UnionSchema {

  public static UnionSchemaImpl create(
      final ImmutableList<Schema> branchSchemas
  ) {
    return new UnionSchemaImpl(branchSchemas);
  }

  private final ImmutableList<Schema> mBranchSchemas;

  public UnionSchemaImpl(
      final ImmutableList<Schema> branchSchemas
  ) {
    mBranchSchemas = branchSchemas;
  }

  @Override
  public Type getType() {
    return Type.UNION;
  }

  @Override
  public UnionSchema asUnionSchema() {
    return this;
  }

  @Override
  public ImmutableList<Schema> getBranchSchemas() {
    return mBranchSchemas;
  }
}
