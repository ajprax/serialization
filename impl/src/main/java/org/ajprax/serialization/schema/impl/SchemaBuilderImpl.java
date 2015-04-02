package org.ajprax.serialization.schema.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.Schema.Type;
import org.ajprax.serialization.schema.SchemaBuilder;

public final class SchemaBuilderImpl implements SchemaBuilder {

  public static final class SchemaBuilderImplProvider implements SchemaBuilder.Provider {

    @Override
    public SchemaBuilder builder(final Type type) {
      return SchemaBuilderImpl.create(type);
    }
  }

  private static Set<Schema.Type> PRIMITIVE_SCHEMA_TYPES = Sets.newHashSet(
      Schema.Type.UNSIGNED_8,
      Schema.Type.UNSIGNED_16,
      Schema.Type.UNSIGNED_32,
      Schema.Type.UNSIGNED_64,
      Schema.Type.UNSIGNED_BIG,
      Schema.Type.SIGNED_8,
      Schema.Type.SIGNED_16,
      Schema.Type.SIGNED_32,
      Schema.Type.SIGNED_64,
      Schema.Type.SIGNED_BIG,
      Schema.Type.FLOAT_32,
      Schema.Type.FLOAT_64,
      Schema.Type.FLOAT_BIG,
      Schema.Type.BOOLEAN,
      Schema.Type.STRING
  );

  private static Set<Schema.Type> ELEMENT_SCHEMA_TYPES = Sets.newHashSet(
      Schema.Type.ARRAY,
      Schema.Type.FIXED_SIZE_ARRAY,
      Schema.Type.OPTIONAL,
      Schema.Type.SET
  );

  private static Set<Schema.Type> NAMED_SCHEMA_TYPES = Sets.newHashSet(
      Schema.Type.ENUM,
      Schema.Type.RECORD
  );

  public static SchemaBuilderImpl create(
      final Schema.Type type
  ) {
    return new SchemaBuilderImpl(type);
  }

  private final Schema.Type mType;
  private Schema mElementSchema = null;
  private Integer mSize = null;
  private String mName = null;
  private ImmutableSet<String> mEnumSymbols = null;
  private Schema mTagSchema = null;
  private Schema mKeySchema = null;
  private Schema mValueSchema = null;
  private Map<String, Schema> mFieldSchemas = Maps.newHashMap();
  private RecordSchemaImpl mPlaceholderSchema = null;
  private List<Schema> mBranchSchemas = Lists.newArrayList();

  private SchemaBuilderImpl(
      final Schema.Type type
  ) {
    mType = type;
  }

  @Override
  public Schema.Type getType() {
    return mType;
  }

  @Override
  public SchemaBuilder setElementSchema(final Schema elementSchema) {
    Preconditions.checkState(
        ELEMENT_SCHEMA_TYPES.contains(mType),
        "setElementSchema is only valid for Schema types in: '%s'.",
        ELEMENT_SCHEMA_TYPES
    );
    Preconditions.checkState(
        null == mElementSchema,
        "Element schema already set to: '%s'.",
        mElementSchema
    );
    mElementSchema = elementSchema;
    return this;
  }

  @Override
  public Schema getElementSchema() {
    Preconditions.checkState(
        ELEMENT_SCHEMA_TYPES.contains(mType),
        "getElementSchema is only valid for Schema types in: '%s'.",
        ELEMENT_SCHEMA_TYPES
    );
    return mElementSchema;
  }

  @Override
  public SchemaBuilder setSize(final Integer size) {
    Preconditions.checkState(
        Schema.Type.FIXED_SIZE_ARRAY == mType,
        "setSize is only valid for FIXED_SIZE_ARRAY Schemas."
    );
    Preconditions.checkState(
        null == mSize,
        "Size is already set to: '%s'.",
        mSize
    );
    mSize = size;
    return this;
  }

  @Override
  public Integer getSize() {
    Preconditions.checkState(
        Schema.Type.FIXED_SIZE_ARRAY == mType,
        "getSize is only valid for FIXED_SIZE_ARRAY Schemas."
    );
    return mSize;
  }

  @Override
  public SchemaBuilder setName(final String name) {
    Preconditions.checkState(
        NAMED_SCHEMA_TYPES.contains(mType),
        "setName is only valid for Schema types in: '%s'.",
        NAMED_SCHEMA_TYPES
    );
    Preconditions.checkState(
        null == mName,
        "Name already set to: '%s'.",
        mName
    );
    mName = name;
    return this;
  }

  @Override
  public String getName() {
    // TODO should this return the name of the record that will be built if the necessary fields are available?
    Preconditions.checkState(
        NAMED_SCHEMA_TYPES.contains(mType),
        "setName is only valid for Schema types in: '%s'.",
        NAMED_SCHEMA_TYPES
    );
    return mName;
  }

  @Override
  public SchemaBuilder setEnumSymbols(final ImmutableSet<String> symbols) {
    Preconditions.checkState(
        Schema.Type.ENUM == mType,
        "setEnumSymbols is only valid for ENUM Schemas."
    );
    Preconditions.checkState(
        null == mEnumSymbols,
        "Enum symbols are already set to: '%s'.",
        mEnumSymbols
    );
    mEnumSymbols = symbols;
    return this;
  }

  @Override
  public ImmutableSet<String> getEnumSymbols() {
    Preconditions.checkState(
        Schema.Type.ENUM == mType,
        "getEnumSymbols is only valid for ENUM Schemas."
    );
    return mEnumSymbols;
  }

  @Override
  public SchemaBuilder setTagSchema(final Schema tagSchema) {
    Preconditions.checkState(
        Schema.Type.EXTENSION == mType,
        "setTagSchema is only valid for EXTENSION Schemas."
    );
    Preconditions.checkState(
        null == mTagSchema,
        "Tag Schema is already set to: '%s'.",
        mTagSchema
    );
    mTagSchema = tagSchema;
    return this;
  }

  @Override
  public Schema getTagSchema() {
    Preconditions.checkState(
        Schema.Type.EXTENSION == mType,
        "getTagSchema is only valid for EXTENSION Schemas."
    );
    return mTagSchema;
  }

  @Override
  public SchemaBuilder setKeySchema(final Schema keySchema) {
    Preconditions.checkState(
        Schema.Type.MAP == mType,
        "setKeySchema is only valid for MAP Schemas."
    );
    Preconditions.checkState(
        null == mKeySchema,
        "Key Schema is already set to: '%s'.",
        mKeySchema
    );
    mKeySchema = keySchema;
    return this;
  }

  @Override
  public SchemaBuilder setValueSchema(final Schema valueSchema) {
    Preconditions.checkState(
        Schema.Type.MAP == mType,
        "setValueSchema is only valid for MAP Schemas."
    );
    Preconditions.checkState(
        null == mValueSchema,
        "Value Schema is already set to: '%s'.",
        mValueSchema
    );
    mValueSchema = valueSchema;
    return this;
  }

  @Override
  public Schema getKeySchema() {
    Preconditions.checkState(
        Schema.Type.MAP == mType,
        "getKeySchema is only valid for MAP Schemas."
    );
    return mKeySchema;
  }

  @Override
  public Schema getValueSchema() {
    Preconditions.checkState(
        Schema.Type.MAP == mType,
        "getValueSchema is only valid for MAP Schemas."
    );
    return mValueSchema;
  }

  @Override
  public SchemaBuilder setFieldSchema(
      final String fieldName,
      final Schema fieldSchema
  ) {
    Preconditions.checkState(
        Schema.Type.RECORD == mType,
        "setFieldSchemas is only valid for RECORD Schemas."
    );
    Preconditions.checkState(
        !mFieldSchemas.containsKey(fieldName),
        "Field: '%s' Schema is already set to: '%s'.",
        fieldName,
        mFieldSchemas.get(fieldName)
    );
    mFieldSchemas.put(fieldName, fieldSchema);
    return this;
  }

  @Override
  public Schema getFieldSchema(final String fieldName) {
    Preconditions.checkState(
        Schema.Type.RECORD == mType,
        "getFieldSchema is only valid for RECORD Schemas."
    );
    return mFieldSchemas.get(fieldName);
  }

  @Override
  public ImmutableMap<String, Schema> getFieldSchemas() {
    Preconditions.checkState(
        Schema.Type.RECORD == mType,
        "getFieldSchemas is only valid for RECORD Schemas."
    );
    return ImmutableMap.copyOf(mFieldSchemas);
  }

  @Override
  public Schema getPlaceholderSchema() {
    Preconditions.checkState(
        Schema.Type.RECORD == mType,
        "getPlaceholderSchema is only valid for RECORD Schemas."
    );
    if (null == mPlaceholderSchema) {
      Preconditions.checkState(
          null != mName,
          "May not builder a placeholder Schema with name unset."
      );
      mPlaceholderSchema = RecordSchemaImpl.create(mName);
    }
    return mPlaceholderSchema;
  }

  @Override
  public SchemaBuilder addBranchSchema(final Schema branchSchema) {
    Preconditions.checkState(
        Schema.Type.UNION == mType,
        "addBranchSchema is only valid for UNION Schemas."
    );
    mBranchSchemas.add(branchSchema);
    return this;
  }

  @Override
  public ImmutableList<Schema> getBranchSchemas() {
    Preconditions.checkState(
        Schema.Type.UNION == mType,
        "getBranchSchemas is only valid for UNION Schemas."
    );
    return ImmutableList.copyOf(mBranchSchemas);
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
      case STRING: {
        return PrimitiveSchemaImpl.create(mType);
      }
      case ENUM: {
        Preconditions.checkState(
            null != mName,
            "ENUM Schema requires a name."
        );
        Preconditions.checkState(
            null != mEnumSymbols,
            "ENUM Schema requires a set of symbols."
        );
        return EnumSchemaImpl.create(mName, mEnumSymbols);
      }
      case EXTENSION: {
        Preconditions.checkState(
            null != mTagSchema,
            "EXTENSION Schema requires a tag Schema."
        );
        return ExtensionSchemaImpl.create(mTagSchema);
      }
      case ARRAY: {
        Preconditions.checkState(
            null != mElementSchema,
            "ARRAY Schema requires an element Schema."
        );
        return ArraySchemaImpl.create(mElementSchema);
      }
      case FIXED_SIZE_ARRAY: {
        Preconditions.checkState(
            null != mElementSchema,
            "FIXED_SIZE_ARRAY Schema requires an element Schema."
        );
        Preconditions.checkState(
            null != mSize,
            "FIXED_SIZE_ARRAY Schema requires a size."
        );
        return FixedSizeArraySchemaImpl.create(mSize, mElementSchema);
      }
      case SET: {
        Preconditions.checkState(
            null != mElementSchema,
            "SET Schema requires an element Schema."
        );
        return SetSchemaImpl.create(mElementSchema);
      }
      case MAP: {
        Preconditions.checkState(
            null != mKeySchema,
            "MAP Schema requires a key Schema."
        );
        Preconditions.checkState(
            null != mValueSchema,
            "MAP Schema requires a value Schema."
        );
        return MapSchemaImpl.create(mKeySchema, mValueSchema);
      }
      case UNION: {
        Preconditions.checkState(
            mBranchSchemas.size() >= 2,
            "UNION Schema requires at two or more branch Schemas."
        );
        return UnionSchemaImpl.create(ImmutableList.copyOf(mBranchSchemas));
      }
      case OPTIONAL: {
        Preconditions.checkState(
            null != mElementSchema,
            "OPTIONAL Schema requires an element Schema."
        );
        return OptionalSchemaImpl.create(mElementSchema);
      }
      case RECORD: {
        final ImmutableMap<String, Schema> fieldSchemas = ImmutableMap.copyOf(mFieldSchemas);
        if (null != mPlaceholderSchema) {
          mPlaceholderSchema.fillFieldSchemas(fieldSchemas);
          return mPlaceholderSchema;
        } else {
          Preconditions.checkState(
              null != mName,
              "RECORD Schema requires a name."
          );
          return RecordSchemaImpl.create(mName, fieldSchemas);
        }
      }
      default: throw new RuntimeException(String.format("Unknown schema type: '%s'", mType));
    }
  }
}
