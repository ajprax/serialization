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
        Maps.transformEntries(
            linked.asRecordSchema().getFieldSchemas(),
            (String key, Schema value) -> value.getName()
        )
    );

    Assert.assertEquals(
        linked.getName(),
        linked.asRecordSchema().getFieldSchemas().get("tail").asOptionalSchema().getElementSchema().getName()
    );
  }
}
