package org.ajprax.serialization.schema.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ajprax.serialization.schema.ArraySchema;
import org.ajprax.serialization.schema.MapSchema;
import org.ajprax.serialization.schema.OptionalSchema;
import org.ajprax.serialization.schema.RecordSchema;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SetSchema;
import org.ajprax.serialization.schema.UnionSchema;
import org.junit.Assert;
import org.junit.Test;

public final class TestSchemaBuilder {

  private static void simpleTest(
      Schema.Type type
  ) {
    switch (
        type
    ) {
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
      case EXTENSION: {
        final Schema.Builder builder = AbstractSchema.SchemaBuilder.create(type);
        final Schema schema = builder.build();
        Assert.assertEquals(type, schema.getType());
        break;
      }
      case ENUM: {
        final Schema.Builder builder = AbstractSchema.SchemaBuilder.create(type);
        builder.withName("name");
        final Schema schema = builder.build();
        Assert.assertEquals(type, schema.getType());
        Assert.assertEquals("name", schema.getName());
        break;
      }
      case ARRAY: {
        final Schema.Builder builder = AbstractSchema.SchemaBuilder.create(type);
        builder.withElementSchema(PrimitiveSchemaImpl.create(Schema.Type.UNSIGNED_32));
        final Schema schema = builder.build();
        final ArraySchema arraySchema = schema.asArraySchema();
        Assert.assertEquals(type, arraySchema.getType());
        Assert.assertEquals(Schema.Type.UNSIGNED_32, arraySchema.getElementSchema().getType());
        break;
      }
      case SET: {
        final Schema.Builder builder = AbstractSchema.SchemaBuilder.create(type);
        builder.withElementSchema(PrimitiveSchemaImpl.create(Schema.Type.UNSIGNED_32));
        final Schema schema = builder.build();
        final SetSchema setSchema = schema.asSetSchema();
        Assert.assertEquals(type, setSchema.getType());
        Assert.assertEquals(Schema.Type.UNSIGNED_32, setSchema.getElementSchema().getType());
        break;
      }
      case MAP: {
        final Schema.Builder builder = AbstractSchema.SchemaBuilder.create(type);
        builder.withKeySchema(PrimitiveSchemaImpl.create(Schema.Type.UNSIGNED_32));
        builder.withValueSchema(PrimitiveSchemaImpl.create(Schema.Type.SIGNED_32));
        final Schema schema = builder.build();
        final MapSchema mapSchema = schema.asMapSchema();
        Assert.assertEquals(type, mapSchema.getType());
        Assert.assertEquals(Schema.Type.UNSIGNED_32, mapSchema.getKeySchema().getType());
        Assert.assertEquals(Schema.Type.SIGNED_32, mapSchema.getValueSchema().getType());
        break;
      }
      case UNION: {
        final ImmutableList<Schema> branches = ImmutableList.of(
            PrimitiveSchemaImpl.create(Schema.Type.UNSIGNED_32),
            PrimitiveSchemaImpl.create(Schema.Type.SIGNED_32)
        );

        final Schema.Builder builder = AbstractSchema.SchemaBuilder.create(type);
        builder.withBranchSchemas(branches);
        final Schema schema = builder.build();
        final UnionSchema unionSchema = schema.asUnionSchema();
        Assert.assertEquals(type, unionSchema.getType());
        Assert.assertEquals(branches, unionSchema.getBranchSchemas());
        break;
      }
      case OPTIONAL: {
        final Schema.Builder builder = AbstractSchema.SchemaBuilder.create(type);
        builder.withElementSchema(PrimitiveSchemaImpl.create(Schema.Type.UNSIGNED_32));
        final Schema schema = builder.build();
        final OptionalSchema optionalSchema = schema.asOptionalSchema();
        Assert.assertEquals(type, optionalSchema.getType());
        Assert.assertEquals(Schema.Type.UNSIGNED_32, optionalSchema.getElementSchema().getType());
        break;
      }
      case RECORD: {
        final ImmutableMap<String, Schema> fields = ImmutableMap
            .<String, Schema>builder()
            .put("field1", PrimitiveSchemaImpl.create(Schema.Type.UNSIGNED_32))
            .build();

        final Schema.Builder builder = AbstractSchema.SchemaBuilder.create(type);
        builder.withName("name");
        builder.withFieldSchemas(fields);
        final Schema schema = builder.build();
        final RecordSchema recordSchema = schema.asRecordSchema();
        Assert.assertEquals(type, recordSchema.getType());
        Assert.assertEquals("name", recordSchema.getName());
        Assert.assertEquals(fields, recordSchema.getFieldSchemas());
        break;
      }
      default: {
        Assert.fail(String.format("Unknown type '%s'", type.name()));
      }
    }
  }

  @Test
  public void testSimpleBuilds() {
    for (Schema.Type type : Schema.Type.values()) {
      simpleTest(type);
    }
  }
}
