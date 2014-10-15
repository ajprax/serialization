package org.ajprax.serialization.schema.impl;

import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;
import org.ajprax.serialization.schema.UnionSchema;

public class UnionSchemaBuilderImpl implements SchemaBuilder.UnionSchemaBuilder {

  public static SchemaBuilder.UnionSchemaBuilder create() {
    return new UnionSchemaBuilderImpl();
  }

  private final List<Schema> mBranchSchemas = Lists.newArrayList();
  private final Set<String> mBranchNames = Sets.newHashSet();

  @Override
  public UnionSchemaBuilderImpl addBranchSchema(
      final Schema branchSchema
  ) {
    Preconditions.checkState(
        !mBranchNames.contains(branchSchema.getName()),
        "Branch schemas already include a branch named '%s'",
        branchSchema
    );
    mBranchSchemas.add(branchSchema);
    mBranchNames.add(branchSchema.getName());
    return this;
  }

  @Override
  public ImmutableList<Schema> getBranchSchemas() {
    return ImmutableList.copyOf(mBranchSchemas);
  }

  @Override
  public UnionSchema build() {
    Preconditions.checkState(
        mBranchSchemas.size() >= 2,
        "UnionSchema may not be built with fewer than two branch schemas."
    );
    return UnionSchemaImpl.create(ImmutableList.copyOf(mBranchSchemas));
  }
}
