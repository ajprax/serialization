package org.ajprax.serialization.schema.impl;

import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.UnionSchema;

public class UnionSchemaImpl extends AbstractSchema implements UnionSchema {

  public static UnionSchemaImpl create(
      final ImmutableList<Schema> branchSchemas
  ) {
    return new UnionSchemaImpl(branchSchemas);
  }

  private static String nameFormat(
      final int branchCount
  ) {
    final StringBuilder sb = new StringBuilder("union<%s");
    for (int i = 1; i < branchCount; i++) {
      sb.append(", %s");
    }
    sb.append(">");
    return sb.toString();
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
  public String getName() {
    return String.format(
        nameFormat(mBranchSchemas.size()),
        Lists.transform(
            mBranchSchemas,
            (Schema branchSchema) -> branchSchema.getName()
        ).toArray()
    );
  }

  @Override
  public UnionSchema asUnionSchema() {
    return this;
  }

  @Override
  public ImmutableList<Schema> getBranchSchemas() {
    return mBranchSchemas;
  }

  @Override
  public boolean recursiveEquals(
      final Object obj,
      final ImmutableSet<String> parentRecordNames
  ) {
    if (obj == null || !(obj instanceof UnionSchema)) {
      return false;
    } else {
      final UnionSchema that = (UnionSchema) obj;
      final boolean typesMatch = Objects.equals(this.getType(), that.getType());
      final boolean branchCountMatches =
          this.getBranchSchemas().size() == that.getBranchSchemas().size();
      int index = 0;
      boolean allBranchesMatch = true;
      for (Schema branchSchema : this.getBranchSchemas()) {
        allBranchesMatch = allBranchesMatch
            && branchSchema.recursiveEquals(that.getBranchSchemas().get(index), parentRecordNames);
        index++;
      }
      return typesMatch && branchCountMatches && allBranchesMatch;
    }
  }
}
