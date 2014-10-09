package org.ajprax.serialization.schema.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ajprax.serialization.errors.BuilderStateException;
import org.ajprax.serialization.errors.TypeMismatchException;
import org.ajprax.serialization.schema.ArraySchema;
import org.ajprax.serialization.schema.MapSchema;
import org.ajprax.serialization.schema.OptionalSchema;
import org.ajprax.serialization.schema.RecordSchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SetSchema;
import org.ajprax.serialization.schema.UnionSchema;

public abstract class AbstractSchema implements Schema {

  public static final class SchemaBuilder implements Schema.Builder {

    public static SchemaBuilder create(
        final Type type
    ) {
      return new SchemaBuilder(type);
    }

    private static void assertUnset(
        Object field,
        String name
    ) {
      Preconditions.checkState(
          null == field,
          String.format("'%s' is already to: '%s'", name, field)
      );
    }

    private final Type mType;
    private String mName = null;
    private Schema mElementSchema = null;
    private Schema mKeySchema = null;
    private Schema mValueSchema = null;
    private ImmutableList<Schema> mBranchSchemas = null;
    private ImmutableMap<String, Schema> mFieldSchemas = null;

    public SchemaBuilder(
        final Type type
    ) {
      mType = type;
    }

    @Override
    public Type getType() {
      return mType;
    }

    @Override
    public Builder withName(
        final String name
    ) {
      switch (mType) {
        case ENUM:
        case RECORD: {
          assertUnset(mName, "name");
          mName = name;
          return this;
        }
        default: {
          throw new BuilderStateException(
              String.format("Type '%s' does not allow custom names.", mType.name())
          );
        }
      }
    }

    @Override
    public String getName() {
      return mName;
    }

    @Override
    public Builder withElementSchema(
        final Schema elementSchema
    ) {
      switch (mType) {
        case ARRAY:
        case SET:
        case OPTIONAL: {
          assertUnset(mElementSchema, "element schema");
          mElementSchema = elementSchema;
          return this;
        }
        default: {
          throw new BuilderStateException(
              String.format("Type '%s' does not accept an element schema.", mType.name())
          );
        }
      }
    }

    @Override
    public Schema getElementSchema() {
      return mElementSchema;
    }

    @Override
    public Builder withKeySchema(
        final Schema keySchema
    ) {
      switch (mType) {
        case MAP: {
          assertUnset(mKeySchema, "key schema");
          mKeySchema = keySchema;
          return this;
        }
        default: {
          throw new BuilderStateException(
              String.format("Type '%s' does not accept a key schema.", mType.name())
          );
        }
      }
    }

    @Override
    public Schema getKeySchema() {
      return mKeySchema;
    }

    @Override
    public Builder withValueSchema(
        final Schema valueSchema
    ) {
      switch (mType) {
        case MAP: {
          assertUnset(mValueSchema, "value schema");
          mValueSchema = valueSchema;
          return this;
        }
        default: {
          throw new BuilderStateException(
              String.format("Type '%s' does not accept a value schema.", mType.name())
          );
        }
      }
    }

    @Override
    public Schema getValueSchema() {
      return mValueSchema;
    }

    @Override
    public Builder withBranchSchemas(
        final ImmutableList<Schema> branchSchemas
    ) {
      switch (mType) {
        case UNION: {
          assertUnset(mBranchSchemas, "branch schemas");
          mBranchSchemas = branchSchemas;
          return this;
        }
        default: {
          throw new BuilderStateException(
              String.format("Type '%s' does not accept branch schemas.", mType.name())
          );
        }
      }
    }

    @Override
    public ImmutableList<Schema> getBranchSchemas() {
      return mBranchSchemas;
    }

    @Override
    public Builder withFieldSchemas(
        final ImmutableMap<String, Schema> fieldSchemas
    ) {
      switch (mType) {
        case RECORD: {
          assertUnset(mFieldSchemas, "field schemas");
          mFieldSchemas = fieldSchemas;
          return this;
        }
        default: {
          throw new BuilderStateException(
              String.format("Type '%s' does not accept field schemas.", mType.name())
          );
        }
      }
    }

    @Override
    public ImmutableMap<String, Schema> getFieldSchemas() {
      return mFieldSchemas;
    }

    @Override
    public Schema build() {
      switch (mType) {
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
        case STRING:
        case EXTENSION: return PrimitiveSchemaImpl.create(mType);
        case ENUM: return EnumSchemaImpl.create(mName);
        case ARRAY: return ArraySchemaImpl.create(mElementSchema);
        case SET: return SetSchemaImpl.create(mElementSchema);
        case MAP: return MapSchemaImpl.create(mKeySchema, mValueSchema);
        case UNION: return UnionSchemaImpl.create(mBranchSchemas);
        case OPTIONAL: return OptionalSchemaImpl.create(mElementSchema);
        case RECORD: return RecordSchemaImpl.create(mName, mFieldSchemas);
        default: throw new BuilderStateException(String.format("Unknown type '%s'", mType));
      }
    }
  }

  @Override
  public String getName() {
    return getType().name();
  }

  @Override
  public ArraySchema asArraySchema() {
    throw new TypeMismatchException(String.format("Schema named %s of type %s is not an Array.", getName(), getType()));
  }

  @Override
  public SetSchema asSetSchema() {
    throw new TypeMismatchException(String.format("Schema named %s of type %s is not a Set.", getName(), getType()));
  }

  @Override
  public MapSchema asMapSchema() {
    throw new TypeMismatchException(String.format("Schema named %s of type %s is not a Map.", getName(), getType()));
  }

  @Override
  public UnionSchema asUnionSchema() {
    throw new TypeMismatchException(String.format("Schema named %s of type %s is not a Union.", getName(), getType()));
  }

  @Override
  public OptionalSchema asOptionalSchema() {
    throw new TypeMismatchException(String.format("Schema named %s of type %s is not Optional.", getName(), getType()));
  }

  @Override
  public RecordSchema asRecordSchema() {
    throw new TypeMismatchException(String.format("Schema named %s of type %s is not a Record.", getName(), getType()));
  }
}
