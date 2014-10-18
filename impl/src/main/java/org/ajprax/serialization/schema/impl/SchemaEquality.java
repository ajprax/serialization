package org.ajprax.serialization.schema.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import org.ajprax.serialization.schema.Schema;

public final class SchemaEquality {

  private static final class SchemaPair {
    private final Schema mLeft;
    private final Schema mRight;

    private SchemaPair(
        final Schema left,
        final Schema right
    ) {
      mLeft = left;
      mRight = right;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(getClass())
          .add("left", mLeft)
          .add("right", mRight)
          .toString();
    }

    @Override
    public int hashCode() {
      return Objects.hash(mLeft, mRight);
    }

    @Override
    public boolean equals(
        final Object obj
    ) {
      if (obj == null || !obj.getClass().equals(getClass())) {
        return false;
      } else {
        final SchemaPair that = (SchemaPair) obj;
        return (this.mLeft == that.mLeft && this.mRight == that.mRight)
            || (this.mLeft == that.mRight && this.mRight == that.mLeft);
      }
    }
  }

  public static SchemaEquality create() {
    return new SchemaEquality();
  }

  private final Map<SchemaPair, Optional<Boolean>> mKnownPairs = Maps.newHashMap();

  private SchemaEquality() { }

  public boolean schemasEqual(
      final Schema left,
      final Schema right
  ) {
    if (!Objects.equals(left.getType(), right.getType())) {
      return false;
    } else {
      final SchemaPair pair = new SchemaPair(left, right);
      final Optional<Boolean> knownEquals = mKnownPairs.get(pair);
      if (knownEquals != null) {
        if (knownEquals.isPresent()) {
          // TODO is it ever possible to get into a situation where we are comparing two schemas we
          // know to be unequal with in a single schema graph comparison? Should a single false
          // short circuit the entire process?
          return knownEquals.get();
        } else {
          // Comparison is in progress, return true to break recursion.
          return true;
        }
      } else {
        switch(left.getType()) {
          case UNSIGNED_8:
          case UNSIGNED_16:
          case UNSIGNED_32:
          case UNSIGNED_64:
          case UNSIGNED_BIG:
          case SIGNED_8:
          case SIGNED_16:
          case SIGNED_32:
          case SIGNED_64:
          case SIGNED_BIG:
          case FLOAT_32:
          case FLOAT_64:
          case FLOAT_BIG:
          case BOOLEAN:
          case STRING: {
            // primitive schemas are equal if their types are equal.
            mKnownPairs.put(pair, Optional.of(true));
            return true;
          }
          case ENUM: {
            // enum schemas are equal if their name and value sets are equal.
            final boolean equal = Objects.equals(left.getName(), right.getName())
                && Objects.equals(left.asEnumSchema().getValues(), right.asEnumSchema().getValues());
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case EXTENSION: {
            // extension schemas are equal if their tag schemas are equal.
            mKnownPairs.put(pair, Optional.empty());
            final boolean equal = schemasEqual(left.asExtensionSchema().getTagSchema(), right.asExtensionSchema().getTagSchema());
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case ARRAY: {
            // array schemas are equal if their element schemas are equal.
            mKnownPairs.put(pair, Optional.empty());
            final boolean equal = schemasEqual(left.asArraySchema().getElementSchema(), right.asArraySchema().getElementSchema());
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case FIXED_SIZE_ARRAY: {
            // fixed size array schema are equal if their size and element schemas are equal.
            mKnownPairs.put(pair, Optional.empty());
            final boolean equal = left.asFixedSizeArraySchema().getSize() == right.asFixedSizeArraySchema().getSize()
                && schemasEqual(left.asFixedSizeArraySchema().getElementSchema(), right.asFixedSizeArraySchema().getElementSchema());
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case SET: {
            // set schemas are equal if their element schemas are equal.
            mKnownPairs.put(pair, Optional.empty());
            final boolean equal = schemasEqual(left.asSetSchema().getElementSchema(), right.asSetSchema().getElementSchema());
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case MAP: {
            // map schemas are equal if the key and value schemas are equal.
            mKnownPairs.put(pair, Optional.empty());
            final boolean equal = schemasEqual(left.asMapSchema().getKeySchema(), right.asMapSchema().getKeySchema())
                && schemasEqual(left.asMapSchema().getValueSchema(), right.asMapSchema().getValueSchema());
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case UNION: {
            // union schemas are equal if their branch schemas are equal.
            mKnownPairs.put(pair, Optional.empty());
            final List<Schema> leftBranchSchemas = left.asUnionSchema().getBranchSchemas();
            final List<Schema> rightBranchSchemas = right.asUnionSchema().getBranchSchemas();
            boolean equal = leftBranchSchemas.size() == rightBranchSchemas.size();
            for (int i = 0; i < leftBranchSchemas.size(); i++) {
              equal = equal && schemasEqual(leftBranchSchemas.get(i), rightBranchSchemas.get(i));
            }
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case OPTIONAL: {
            // optional schemas are equal if their element schemas are equal.
            mKnownPairs.put(pair, Optional.empty());
            final boolean equal = schemasEqual(left.asOptionalSchema().getElementSchema(), right.asOptionalSchema().getElementSchema());
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case RECORD: {
            // record schemas are equal if their names are equal and their field schemas are equal.
            mKnownPairs.put(pair, Optional.empty());
            final Map<String, Schema> leftFieldSchemas = left.asRecordSchema().getFieldSchemas();
            final Map<String, Schema> rightFieldSchemas = right.asRecordSchema().getFieldSchemas();
            boolean equal = Objects.equals(leftFieldSchemas.keySet(), rightFieldSchemas.keySet());
            for (String fieldName : leftFieldSchemas.keySet()) {
              equal = equal && schemasEqual(leftFieldSchemas.get(fieldName), rightFieldSchemas.get(fieldName));
            }
            mKnownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          default: throw new RuntimeException(String.format("Unknown schema type: '%s'", left.getType()));
        }
      }
    }
  }
}
