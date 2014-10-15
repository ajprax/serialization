package org.ajprax.serialization.schema;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public interface SchemaBuilder {

  Schema.Type getType();

  ArraySchemaBuilder asArraySchemaBuilder();
  FixedSizeArraySchemaBuilder asFixedSizeArraySchemaBuilder();
  EnumSchemaBuilder asEnumSchemaBuilder();
  ExtensionSchemaBuilder asExtensionSchemaBuilder();
  MapSchemaBuilder asMapSchemaBuilder();
  OptionalSchemaBuilder asOptionalSchemaBuilder();
  RecordSchemaBuilder asRecordSchemaBuilder();
  SetSchemaBuilder asSetSchemaBuilder();
  UnionSchemaBuilder asUnionSchemaBuilder();

  Schema build();

  public static interface ArraySchemaBuilder {
    ArraySchemaBuilder setElementSchema(Schema elementSchema);
    Schema getElementSchema();
    ArraySchema build();
  }
  public static interface FixedSizeArraySchemaBuilder {
    FixedSizeArraySchemaBuilder setSize(Integer size);
    FixedSizeArraySchemaBuilder setElementSchema(Schema elementSchema);
    Integer getSize();
    Schema getElementSchema();
    FixedSizeArraySchema build();
  }
  public static interface EnumSchemaBuilder {
    EnumSchemaBuilder setName(String name);
    EnumSchemaBuilder setValues(ImmutableSet<String> values);
    String getName();
    ImmutableSet<String> getValues();
    EnumSchema build();
  }
  public static interface ExtensionSchemaBuilder {
    ExtensionSchemaBuilder setTagSchema(Schema tagSchema);
    Schema getTagSchema();
    ExtensionSchema build();
  }
  public static interface MapSchemaBuilder {
    MapSchemaBuilder setKeySchema(Schema keySchema);
    MapSchemaBuilder setValueSchema(Schema valueSchema);
    Schema getKeySchema();
    Schema getValueSchema();
    MapSchema build();
  }
  public static interface OptionalSchemaBuilder {
    OptionalSchemaBuilder setElementSchema(Schema elementSchema);
    Schema getElementSchema();
    OptionalSchema build();
  }
  public static interface RecordSchemaBuilder {
    RecordSchemaBuilder setName(String name);
    RecordSchemaBuilder setFieldSchema(String fieldName, Schema fieldSchema);
    String getName();
    Schema getFieldSchema(String fieldName);
    ImmutableMap<String, Schema> getFieldSchemas();
    Schema getPlaceholderSchema();
    RecordSchema build();
  }
  public static interface SetSchemaBuilder {
    SetSchemaBuilder setElementSchema(Schema elementSchema);
    Schema getElementSchema();
    SetSchema build();
  }
  public static interface UnionSchemaBuilder {
    UnionSchemaBuilder addBranchSchema(Schema branchSchema);
    ImmutableList<Schema> getBranchSchemas();
    UnionSchema build();
  }
}
