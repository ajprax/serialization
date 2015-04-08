package org.ajprax.serialization.schema.impl;

import com.google.common.collect.ImmutableSet;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.Schema.Type;

public class Temp {
  private static Schema optional(
      final Schema schema
  ) {
    return Schema.builder(Type.OPTIONAL).setElementSchema(schema).build();
  }

  public static final Schema SCHEMA_SCHEMA;
  static {
    final Schema.Builder builder = Schema.builder(Type.RECORD).setName("schema");
    final Schema nameSchema = optional(Schema.primitive(Type.STRING));
    final Schema enumSymbolsSchema = optional(
        Schema.builder(Type.SET)
            .setElementSchema(Schema.primitive(Type.STRING))
            .build()
    );
    final Schema tagSchemaSchema = optional(builder.getPlaceholderSchema());
    final Schema sizeSchema = optional(Schema.primitive(Type.SIGNED_32)); // TODO this makes more sense as unsigned, but it's not supported in Java.
    final Schema keySchemaSchema = optional(builder.getPlaceholderSchema());
    final Schema valueSchemaSchema = optional(builder.getPlaceholderSchema());
    final Schema branchSchemasSchema = optional(
        Schema.builder(Type.ARRAY)
            .setElementSchema(builder.getPlaceholderSchema())
            .build()
    );
    final Schema fieldSchemasSchema = optional(
        Schema.builder(Type.MAP)
            .setKeySchema(Schema.primitive(Type.STRING))
            .setValueSchema(builder.getPlaceholderSchema())
            .build()
    );
    final Schema elementSchemaSchema = optional(builder.getPlaceholderSchema());

    SCHEMA_SCHEMA = builder
        .setFieldSchema(
            "type",
            Schema.builder(Type.ENUM)
                .setName("type")
                .setEnumSymbols(
                    ImmutableSet.of(
                        "UNSIGNED_8",
                        "UNSIGNED_16",
                        "UNSIGNED_32",
                        "UNSIGNED_64",
                        "UNSIGNED_BIG",
                        "SIGNED_8",
                        "SIGNED_16",
                        "SIGNED_32",
                        "SIGNED_64",
                        "SIGNED_BIG",
                        "FLOAT_32",
                        "FLOAT_64",
                        "FLOAT_BIG",
                        "BOOLEAN",
                        "STRING",
                        "ENUM",
                        "EXTENSION",
                        "ARRAY",
                        "FIXED_SIZE_ARRAY",
                        "SET",
                        "MAP",
                        "UNION",
                        "OPTIONAL",
                        "RECORD"
                    )
                )
                .build()
        )
        .setFieldSchema("name", nameSchema)
        .setFieldSchema("enum_symbols", enumSymbolsSchema)
        .setFieldSchema("tag_schema", tagSchemaSchema)
        .setFieldSchema("size", sizeSchema)
        .setFieldSchema("key_schema", keySchemaSchema)
        .setFieldSchema("value_schema", valueSchemaSchema)
        .setFieldSchema("branch_schemas", branchSchemasSchema)
        .setFieldSchema("field_schemas", fieldSchemasSchema)
        .setFieldSchema("element_schema", elementSchemaSchema)
        .build();
  }

  /* TODO can't create GenericRecords for the API package for now.
  private static GenericRecord primitive(
      final Schema.Type type
  ) {

  }

  public static GenericRecord schemaRecord(
      final Schema schema
  ) {
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
      case STRING:
      case ENUM:
      case EXTENSION:
      case ARRAY:
      case FIXED_SIZE_ARRAY:
      case SET:
      case MAP:
      case UNION:
      case OPTIONAL:
      case RECORD:
      default: throw new RuntimeException(String.format("Unknown schema type: '%s'", schema.getType()));
    }
  }
  */
}
