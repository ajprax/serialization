package org.ajprax.serialization.schema.impl;

import com.google.common.collect.Maps;
import org.ajprax.serialization.schema.Schema;
import org.junit.Assert;
import org.junit.Test;

public class TestRecordSchemaBuilderImpl {
  @Test
  public void testRecursiveRecord() {
    final Schema.Builder builder = SchemaBuilderImpl.create(Schema.Type.RECORD);
    final Schema linked = builder
        .setName("LinkedBooleanList")
        .setFieldSchema(
            "head",
            SchemaBuilderImpl.create(Schema.Type.BOOLEAN).build()
        )
        .setFieldSchema(
            "tail",
            SchemaBuilderImpl.create(Schema.Type.OPTIONAL).setElementSchema(builder.getPlaceholderSchema()).build()
        )
        .build();

    System.out.println(linked.getName());
    System.out.println(
        Maps.transformValues(
            linked.getFieldSchemas(),
            Schema::getName
        )
    );

    Assert.assertEquals(
        linked.getName(),
        linked.getFieldSchemas().get("tail").getElementSchema().getName()
    );

    final Schema.Builder rsb = SchemaBuilderImpl.create(Schema.Type.RECORD);
    final Schema linkedList = rsb
        .setName("LinkedList")
        .setFieldSchema(
            "head",
            SchemaBuilderImpl
                .create(Schema.Type.EXTENSION)

                .setTagSchema(Schema.primitive(Schema.Type.STRING))
                .build()
        )
        .setFieldSchema(
            "tail",
            SchemaBuilderImpl.create(Schema.Type.OPTIONAL)

                .setElementSchema(rsb.getPlaceholderSchema())
                .build()
        )
        .build();

    final Schema.Builder rsb2 = SchemaBuilderImpl.create(Schema.Type.RECORD);
    final Schema linkedList2 = rsb2
        .setName("LinkedList")
        .setFieldSchema(
            "head",
            SchemaBuilderImpl
                .create(Schema.Type.EXTENSION)

                .setTagSchema(Schema.primitive(Schema.Type.STRING))
                .build()
        )
        .setFieldSchema(
            "tail",
            SchemaBuilderImpl.create(Schema.Type.OPTIONAL)

                .setElementSchema(rsb2.getPlaceholderSchema())
                .build()
        )
        .build();

    Assert.assertEquals(linkedList, linkedList2);
    System.out.println(linkedList.hashCode());
    System.out.println(linkedList2.hashCode());
    System.out.println(linkedList.toString());
    System.out.println(linkedList2.toString());

    final Schema.Builder rsb3 = SchemaBuilderImpl.create(Schema.Type.RECORD);
    final Schema linkedList3 = rsb3
        .setName("LinkedList3")
        .setFieldSchema(
            "head",
            Schema.primitive(Schema.Type.BOOLEAN)
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
