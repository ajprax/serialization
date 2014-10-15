package org.ajprax.serialization.schema.impl;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;

public final class SchemaBuilderImpl implements SchemaBuilder {

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

  public static SchemaBuilderImpl create(
      final Schema.Type type
  ) {
    return new SchemaBuilderImpl(type);
  }

  private final Schema.Type mType;

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
  public ArraySchemaBuilder asArraySchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.ARRAY,
        "Builder with type '%s' may not be cast to ArraySchemaBuilder.",
        mType
    );
    return ArraySchemaBuilderImpl.create();
  }

  @Override
  public FixedSizeArraySchemaBuilder asFixedSizeArraySchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.FIXED_SIZE_ARRAY,
        "Builder with type '%s' may not be cast to FixedSizeArraySchemaBuilder.",
        mType
    );
    return FixedSizeArraySchemaBuilderImpl.create();
  }

  @Override
  public EnumSchemaBuilder asEnumSchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.ENUM,
        "Builder with type '%s' may not be cast to EnumSchemaBuilder.",
        mType
    );
    return EnumSchemaBuilderImpl.create();
  }

  @Override
  public ExtensionSchemaBuilder asExtensionSchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.EXTENSION,
        "Builder with type '%s' may not be cast to ExtensionSchemaBuilder.",
        mType
    );
    return ExtensionSchemaBuilderImpl.create();
  }

  @Override
  public MapSchemaBuilder asMapSchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.MAP,
        "Builder with type '%s' may not be cast to MapSchemaBuilder.",
        mType
    );
    return MapSchemaBuilderImpl.create();
  }

  @Override
  public OptionalSchemaBuilder asOptionalSchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.OPTIONAL,
        "Builder with type '%s' may not be cast to OptionalSchemaBuilder.",
        mType
    );
    return OptionalSchemaBuilderImpl.create();
  }

  @Override
  public RecordSchemaBuilder asRecordSchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.RECORD,
        "Builder with type '%s' may not be cast to RecordSchemaBuilder.",
        mType
    );
    return RecordSchemaBuilderImpl.create();
  }

  @Override
  public SetSchemaBuilder asSetSchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.SET,
        "Builder with type '%s' may not be cast to SetSchemaBuilder.",
        mType
    );
    return SetSchemaBuilderImpl.create();
  }

  @Override
  public UnionSchemaBuilder asUnionSchemaBuilder() {
    Preconditions.checkState(
        mType == Schema.Type.UNION,
        "Builder with type '%s' may not be cast to UnionSchemaBuilder.",
        mType
    );
    return UnionSchemaBuilderImpl.create();
  }

  @Override
  public Schema build() {
    Preconditions.checkState(
        PRIMITIVE_SCHEMA_TYPES.contains(mType),
        "Builder with non-primitive type '%s' may not be built directly.",
        mType
    );
    return PrimitiveSchemaImpl.create(mType);
  }
}
