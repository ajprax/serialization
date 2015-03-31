package org.ajprax.serialization.generic.impl;

import com.google.common.collect.ImmutableMap;
import org.ajprax.serialization.generic.GenericExtension;
import org.ajprax.serialization.generic.GenericUnion;
import org.ajprax.serialization.generic.GenericValue;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.Schema.Type;
import org.ajprax.serialization.schema.impl.ExtensionSchemaImpl;
import org.ajprax.serialization.schema.impl.PrimitiveSchemaImpl;
import org.ajprax.serialization.schema.impl.SchemaBuilderImpl;
import org.junit.Test;

public class TestGenerics {

  @Test
  public void test() {
    final GenericValue<String> v = new GenericValue<String>() {
      @Override
      public Schema getSchema() {
        return PrimitiveSchemaImpl.create(Type.STRING);
      }

      @Override
      public String getValue() {
        return "abc";
      }
    };

    final GenericUnion u = new GenericUnion() {
      private final int index = 0;
      private final Schema schema = SchemaBuilderImpl.create(Type.UNION)
          .addBranchSchema(PrimitiveSchemaImpl.create(Type.STRING))
          .addBranchSchema(PrimitiveSchemaImpl.create(Type.BOOLEAN))
          .build();
      private final Object value = "abc";

      public Schema getBranchSchema() {
        return schema.getBranchSchemas().get(index);
      }

      @Override
      public Schema getSchema() {
        return schema;
      }

      @Override
      public Object getValue() {
        return value;
      }

      @SuppressWarnings("unchecked")
      public <T> T getTypedValue() {
        return (T) value;
      }
    };

    System.out.println(v.getSchema());
    System.out.println(v.getValue());
    System.out.println(u.getSchema());
    System.out.println(u.getBranchSchema());
    System.out.println(u.getValue());
    System.out.println(u.<String>getTypedValue());

    final GenericExtension<String, byte[]> e = new GenericExtension<String, byte[]>() {
      @Override
      public String getTag() {
        return "tag";
      }

      @Override
      public Schema getSchema() {
        return ExtensionSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
      }

      @Override
      public byte[] getValue() {
        return new byte[0];
      }
    };

    System.out.println(e.getSchema());
    System.out.println(e.getTag());
    System.out.println(e.getValue());
  }

  @Test
  public void testRecord() {
    final Schema schema = SchemaBuilderImpl.create(Type.RECORD)
        .setName("Fullname")
        .setFieldSchema("first", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("last", PrimitiveSchemaImpl.create(Type.STRING))
        .build();
    final ImmutableMap<String, Object> value = ImmutableMap.of(
        "first", "aaron",
        "last", "feldstein"
    );

    final GenericRecordImpl ri = GenericRecordImpl.create(schema, value);
    System.out.println(ri.getSchema());
    System.out.println(ri.getFieldSchema("first"));
    System.out.println(ri.getValue());
    System.out.println(ri.<String>get("first"));
    System.out.println(ri);
  }
}
