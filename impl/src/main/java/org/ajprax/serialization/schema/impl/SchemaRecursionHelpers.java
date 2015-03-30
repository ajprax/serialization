package org.ajprax.serialization.schema.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.ajprax.serialization.schema.Schema;

/** Helper methods for recursive Schema operations. */
public final class SchemaRecursionHelpers {

  /** Unordered pair of Schemas. Equality is tested by reference rather than value. */
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
      // TODO: Consider removing these safety checks to improve performance.
      // This is a private class which is never called with null values or values of other types.
      if (obj == null || !obj.getClass().equals(getClass())) {
        return false;
      } else {
        final SchemaPair that = (SchemaPair) obj;
        return (this.mLeft == that.mLeft && this.mRight == that.mRight)
            || (this.mLeft == that.mRight && this.mRight == that.mLeft);
      }
    }
  }

  private SchemaRecursionHelpers() { }

  private static boolean equals(
      final Schema left,
      final Schema right,
      final Map<SchemaPair, Optional<Boolean>> knownPairs
  ) {
    if (!Objects.equals(left.getType(), right.getType())) {
      return false;
    } else {
      final SchemaPair pair = new SchemaPair(left, right);
      final Optional<Boolean> knownEquals = knownPairs.get(pair);
      if (knownEquals != null) {
        // TODO is it ever possible to get into a situation where we are comparing two schemas we
        // know to be unequal within a single schema graph comparison? Should a single false
        // short circuit the entire process? In other words, can knownEquals ever contain false?
        // if absent, recursive comparison in progress, break recursion.
        return knownEquals.orElse(true);
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
            knownPairs.put(pair, Optional.of(true));
            return true;
          }
          case ENUM: {
            // enum schemas are equal if their name and value sets are equal.
            final boolean equal = Objects.equals(left.getName(), right.getName())
                && Objects.equals(left.getEnumSymbols(), right.getEnumSymbols());
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case EXTENSION: {
            // extension schemas are equal if their tag schemas are equal.
            knownPairs.put(pair, Optional.empty());
            final boolean equal = equals(left.getTagSchema(), right.getTagSchema(), knownPairs);
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case ARRAY: {
            // array schemas are equal if their element schemas are equal.
            knownPairs.put(pair, Optional.empty());
            final boolean equal = equals(left.getElementSchema(), right.getElementSchema(), knownPairs);
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case FIXED_SIZE_ARRAY: {
            // fixed size array schema are equal if their size and element schemas are equal.
            knownPairs.put(pair, Optional.empty());
            final boolean equal = left.getSize() == right.getSize()
                && equals(left.getElementSchema(), right.getElementSchema(), knownPairs);
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case SET: {
            // set schemas are equal if their element schemas are equal.
            knownPairs.put(pair, Optional.empty());
            final boolean equal = equals(left.getElementSchema(), right.getElementSchema(), knownPairs);
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case MAP: {
            // map schemas are equal if the key and value schemas are equal.
            knownPairs.put(pair, Optional.empty());
            final boolean equal = equals(left.getKeySchema(), right.getKeySchema(), knownPairs)
                && equals(left.getValueSchema(), right.getValueSchema(), knownPairs);
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case UNION: {
            // union schemas are equal if their branch schemas are equal.
            knownPairs.put(pair, Optional.empty());
            final List<Schema> leftBranchSchemas = left.getBranchSchemas();
            final List<Schema> rightBranchSchemas = right.getBranchSchemas();
            boolean equal = leftBranchSchemas.size() == rightBranchSchemas.size();
            for (int i = 0; i < leftBranchSchemas.size(); i++) {
              equal = equal && equals(leftBranchSchemas.get(i), rightBranchSchemas.get(i), knownPairs);
            }
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case OPTIONAL: {
            // optional schemas are equal if their element schemas are equal.
            knownPairs.put(pair, Optional.empty());
            final boolean equal = equals(left.getElementSchema(), right.getElementSchema(), knownPairs);
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          case RECORD: {
            // record schemas are equal if their names are equal and their field names and schemas are equal.
            knownPairs.put(pair, Optional.empty());
            final Map<String, Schema> leftFieldSchemas = left.getFieldSchemas();
            final Map<String, Schema> rightFieldSchemas = right.getFieldSchemas();
            boolean equal = Objects.equals(leftFieldSchemas.keySet(), rightFieldSchemas.keySet());
            for (String fieldName : leftFieldSchemas.keySet()) {
              equal = equal && equals(leftFieldSchemas.get(fieldName), rightFieldSchemas.get(fieldName), knownPairs);
            }
            knownPairs.put(pair, Optional.of(equal));
            return equal;
          }
          default: throw new RuntimeException(String.format("Unknown schema type: '%s'", left.getType()));
        }
      }
    }
  }

  private static String toString(
      final Schema schema,
      final Map<Schema, Optional<String>> knownSchemas
  ) {
    final Optional<String> knownToString = knownSchemas.get(schema);
    if (knownToString != null) {
      // if absent, recursive toString in progress, break recursion.
      return knownToString.orElse(schema.getName());
    } else {
      knownSchemas.put(schema, Optional.empty());
      switch (schema.getType()) {
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
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case ENUM: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add("name", schema.getName())
              .add("values", schema.getEnumSymbols())
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case EXTENSION: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add("tag_schema", toString(schema.getTagSchema(), knownSchemas))
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case ARRAY: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add("element_schema", toString(schema.getElementSchema(), knownSchemas))
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case FIXED_SIZE_ARRAY: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add("size", schema.getSize())
              .add("element_schema", toString(schema.getElementSchema(), knownSchemas))
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case SET: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add("element_schema", toString(schema.getElementSchema(), knownSchemas))
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case MAP: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add("key_schema", toString(schema.getKeySchema(), knownSchemas))
              .add("value_schema", toString(schema.getValueSchema(), knownSchemas))
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case UNION: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add(
                  "branch_schemas",
                  Lists.transform(
                      schema.getBranchSchemas(),
                      (Schema branchSchema) -> toString(branchSchema, knownSchemas)
                  )
              )
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case OPTIONAL: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add("element_schema", toString(schema.getElementSchema(), knownSchemas))
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        case RECORD: {
          final String toString = MoreObjects.toStringHelper(schema.getClass())
              .add("type", schema.getType())
              .add("name", schema.getName())
              .add(
                  "field_schemas",
                  Maps.transformValues(
                      schema.getFieldSchemas(),
                      (Schema fieldSchema) -> toString(fieldSchema, knownSchemas)
                  )
              )
              .toString();
          knownSchemas.put(schema, Optional.of(toString));
          return toString;
        }
        default: throw new RuntimeException(String.format("Unknown schema type: '%s'.", schema.getType()));
      }
    }
  }

  private static int hashCode(
      final Schema schema,
      final Map<Schema, Optional<Integer>> knownSchemas
  ) {
    final Optional<Integer> knownHashCode = knownSchemas.get(schema);
    if (knownHashCode != null) {
      // if absent, recursive hashCode in progress, break recursion.
      return knownHashCode.orElse(0);
    } else {
      knownSchemas.put(schema, Optional.empty());
      switch (schema.getType()) {
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
          final int hashCode = Objects.hashCode(schema.getType());
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case ENUM: {
          final int hashCode = Objects.hash(schema.getType(), schema.getName(), schema.getEnumSymbols());
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case EXTENSION: {
          final int hashCode = Objects.hash(schema.getType(), hashCode(schema.getTagSchema(), knownSchemas));
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case ARRAY: {
          final int hashCode = Objects.hash(schema.getType(), hashCode(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case FIXED_SIZE_ARRAY: {
          final int hashCode = Objects.hash(schema.getType(), schema.getSize(), hashCode(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case SET: {
          final int hashCode = Objects.hash(schema.getType(), hashCode(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case MAP: {
          final int hashCode = Objects.hash(schema.getType(), hashCode(schema.getKeySchema(), knownSchemas), hashCode(schema.getValueSchema()));
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case UNION: {
          final int hashCode = Objects.hash(schema.getType(), Lists.transform(schema.getBranchSchemas(), (Schema branchSchema) -> hashCode(branchSchema, knownSchemas)));
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case OPTIONAL: {
          final int hashCode = Objects.hash(schema.getType(), hashCode(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        case RECORD: {
          final int hashCode = Objects.hash(schema.getType(), schema.getName(), Maps.transformValues(schema.getFieldSchemas(), (Schema fieldSchema) -> hashCode(fieldSchema, knownSchemas)));
          knownSchemas.put(schema, Optional.of(hashCode));
          return hashCode;
        }
        default: throw new RuntimeException(String.format("Unknown schema type: '%s'", schema.getType()));
      }
    }
  }

  /**
   * Create a String representation of a Schema.
   *
   * If the Schema contains a recursive reference, the recursion is broken and a token is included
   * in its place that indicates a loop. Recursive loops are identified according to the rules
   * defined by {@link #equals(org.ajprax.serialization.schema.Schema,
   * org.ajprax.serialization.schema.Schema)}.
   *
   * @param schema Schema for which to get a String representation.
   * @return The given Schema represented as a String.
   */
  public static String toString(
      final Schema schema
  ) {
    return toString(schema, Maps.newHashMap());
  }

  /**
   * Hash a Schema to an integer.
   *
   * If the Schema contains a recursive reference, the recursion is broken and the hash value up to
   * that point is returned. Recursive loops are identified by reference equality. This recursive
   * check may require a deeper traversal than the one defined by
   * {@link #equals(org.ajprax.serialization.schema.Schema, org.ajprax.serialization.schema.Schema)}
   * but equals relies on hashCode to break recursion so that it may use a hash map internally. As a
   * result, `hashCode` must use a more fundamental system to break recursion.
   *
   * @param schema Schema to hash to an integer.
   * @return An integer reduction of the given Schema.
   */
  public static int hashCode(
      final Schema schema
  ) {
    return hashCode(schema, Maps.newIdentityHashMap());
  }

  /**
   * Compare two Schemas for equality.
   *
   * Two Schemas are considered equal if neither contains a recursive reference and their type trees
   * are equal or if both contain recursive references at the same place in their respective type
   * graphs and the graphs are otherwise equal.
   *
   * @param left Left Schema to compare.
   * @param right Right Schema to compare.
   * @return Whether the two Schemas are equal.
   */
  public static boolean equals(
      final Schema left,
      final Schema right
  ) {
    return equals(left, right, Maps.newHashMap());
  }
}
