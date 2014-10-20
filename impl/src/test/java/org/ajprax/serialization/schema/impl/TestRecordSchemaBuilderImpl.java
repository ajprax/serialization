package org.ajprax.serialization.schema.impl;

import com.google.common.collect.Maps;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;

public class TestRecordSchemaBuilderImpl {
  @Test
  public void testRecursiveRecord() {
    final SchemaBuilder.RecordSchemaBuilder builder = SchemaBuilderImpl.create(Schema.Type.RECORD).asRecordSchemaBuilder();
    builder.setName("LinkedBooleanList");
    builder.setFieldSchema("head", PrimitiveSchemaImpl.create(Schema.Type.BOOLEAN));
    builder.setFieldSchema("tail", SchemaBuilderImpl.create(Schema.Type.OPTIONAL).asOptionalSchemaBuilder().setElementSchema(builder.getPlaceholderSchema()).build());
    final Schema linked = builder.build();
    System.out.println(linked.getName());
    System.out.println(
        Maps.transformValues(
            linked.asRecordSchema().getFieldSchemas(),
            (Schema value) -> value.getName()
        )
    );

    Assert.assertEquals(
        linked.getName(),
        linked.asRecordSchema().getFieldSchemas().get("tail").asOptionalSchema().getElementSchema().getName()
    );

    final SchemaBuilder.RecordSchemaBuilder rsb = SchemaBuilderImpl.create(Schema.Type.RECORD).asRecordSchemaBuilder();
    final Schema linkedList = rsb
        .setName("LinkedList")
        .setFieldSchema(
            "head",
            SchemaBuilderImpl
                .create(Schema.Type.EXTENSION)
                .asExtensionSchemaBuilder()
                .setTagSchema(PrimitiveSchemaImpl.create(Schema.Type.STRING))
                .build()
        )
        .setFieldSchema(
            "tail",
            SchemaBuilderImpl.create(Schema.Type.OPTIONAL)
                .asOptionalSchemaBuilder()
                .setElementSchema(rsb.getPlaceholderSchema())
                .build()
        )
        .build();

    final SchemaBuilder.RecordSchemaBuilder rsb2 = SchemaBuilderImpl.create(Schema.Type.RECORD).asRecordSchemaBuilder();
    final Schema linkedList2 = rsb2
        .setName("LinkedList")
        .setFieldSchema(
            "head",
            SchemaBuilderImpl
                .create(Schema.Type.EXTENSION)
                .asExtensionSchemaBuilder()
                .setTagSchema(PrimitiveSchemaImpl.create(Schema.Type.STRING))
                .build()
        )
        .setFieldSchema(
            "tail",
            SchemaBuilderImpl.create(Schema.Type.OPTIONAL)
                .asOptionalSchemaBuilder()
                .setElementSchema(rsb2.getPlaceholderSchema())
                .build()
        )
        .build();

    Assert.assertEquals(linkedList, linkedList2);
    System.out.println(linkedList.hashCode());
    System.out.println(linkedList2.hashCode());
    System.out.println(linkedList.toString());
    System.out.println(linkedList2.toString());

    final SchemaBuilder.RecordSchemaBuilder rsb3 = SchemaBuilderImpl.create(Schema.Type.RECORD).asRecordSchemaBuilder();
    final Schema linkedList3 = rsb3
        .setName("LinkedList3")
        .setFieldSchema(
            "head",
            PrimitiveSchemaImpl.create(Schema.Type.BOOLEAN)
        )
        .setFieldSchema(
            "tail",
            rsb3.getPlaceholderSchema()
        )
        .build();
    System.out.println(linkedList3.toString());
    System.out.println(linkedList3.hashCode());
    System.out.println(linkedList3.equals(linkedList2));
  }
}
