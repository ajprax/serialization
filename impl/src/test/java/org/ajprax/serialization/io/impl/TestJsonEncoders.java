package org.ajprax.serialization.io.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.ajprax.serialization.generic.GenericExtension;
import org.ajprax.serialization.generic.GenericRecord;
import org.ajprax.serialization.generic.GenericUnion;
import org.ajprax.serialization.generic.impl.GenericExtensionImpl;
import org.ajprax.serialization.generic.impl.GenericRecordImpl;
import org.ajprax.serialization.generic.impl.GenericUnionImpl;
import org.ajprax.serialization.io.JsonEncoder;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.Schema.Type;
import org.ajprax.serialization.schema.SchemaBuilder;
import org.ajprax.serialization.schema.impl.ArraySchemaImpl;
import org.ajprax.serialization.schema.impl.EnumSchemaImpl;
import org.ajprax.serialization.schema.impl.ExtensionSchemaImpl;
import org.ajprax.serialization.schema.impl.FixedSizeArraySchemaImpl;
import org.ajprax.serialization.schema.impl.MapSchemaImpl;
import org.ajprax.serialization.schema.impl.OptionalSchemaImpl;
import org.ajprax.serialization.schema.impl.PrimitiveSchemaImpl;
import org.ajprax.serialization.schema.impl.SchemaBuilderImpl;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonEncoders {
  @Test
  public void testMap() {
    final Schema mapSchema = MapSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING), PrimitiveSchemaImpl.create(Type.SIGNED_32));
    final Map<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);
    final JsonEncoder<Map<String, Integer>> encoder = JsonEncoders.forSchema(mapSchema);
    final String expected = "[{\"k\":\"a\",\"v\":1},{\"k\":\"b\",\"v\":2}]";
    final String actual = encoder.encode(map).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testNestedArrays() {
    final Schema nestedSchema = ArraySchemaImpl.create(ArraySchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING)));
    final List<List<String>> nested = Lists.newArrayList(Lists.newArrayList(), Lists.newArrayList("a"), Lists.newArrayList("a", "b"));
    final JsonEncoder<List<List<String>>> encoder = JsonEncoders.forSchema(nestedSchema);
    final String expected = "[[],[\"a\"],[\"a\",\"b\"]]";
    final String actual = encoder.encode(nested).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testFixedSizeArrays() {
    final Schema fixedSizeSchema = FixedSizeArraySchemaImpl.create(2, PrimitiveSchemaImpl.create(Type.STRING));
    final JsonEncoder<List<String>> encoder = JsonEncoders.forSchema(fixedSizeSchema);

    {
      final List<String> correct = Lists.newArrayList("a", "b");
      final String expected = "[\"a\",\"b\"]";
      final String actual = encoder.encode(correct).toString();
      Assert.assertEquals(expected, actual);
    }
    {
      final List<String> incorrect = Lists.newArrayList("a");
      try {
        JsonUtils.prettyPrint(encoder.encode(incorrect));
      } catch (IllegalArgumentException iae) {
        Assert.assertEquals("Input size: '1' does not match fixed size: '2'.", iae.getMessage());
      }
    }
  }

  @Test
  public void testTypeMismatch() {
    final Schema arraySchema = ArraySchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
    final List<Integer> list = Lists.newArrayList(1, 2);
    final JsonEncoder<List<Integer>> encoder = JsonEncoders.forSchema(arraySchema);
    try {
      encoder.encode(list);
    } catch (ClassCastException cce) {
      Assert.assertEquals("java.lang.Integer cannot be cast to java.lang.String", cce.getMessage());
    }
  }

  public enum Weekdays {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
  }

  public enum Months {
    JANUARY, FEBRUARY
  }

  @Test
  public void testEnum() {
    final ImmutableSet<String> values = ImmutableSet.copyOf(ImmutableSet.copyOf(Weekdays.values()).stream().map(Enum::name).collect(Collectors.toSet()));
    final Schema schema = EnumSchemaImpl.create(Weekdays.class.getName(), values);
    final JsonEncoder<Weekdays> encoder = JsonEncoders.forSchema(schema);
    final String expected = String.format("{\"name\":\"%s\",\"value\":\"MONDAY\"}", Weekdays.class.getName());
    final String actual = encoder.encode(Weekdays.MONDAY).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testRecord() {
    final Schema schema = SchemaBuilderImpl.create(Type.RECORD)
        .setName("outer")
        .setFieldSchema("a", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("b", PrimitiveSchemaImpl.create(Type.BOOLEAN))
        .setFieldSchema("c", SchemaBuilderImpl.create(Type.RECORD)
                .setName("inner")
                .setFieldSchema("c1", PrimitiveSchemaImpl.create(Type.SIGNED_16))
                .setFieldSchema("c2", PrimitiveSchemaImpl.create(Type.FLOAT_32))
                .build()
        )
        .build();
    final GenericRecord record = GenericRecordImpl.create(
        schema,
        ImmutableMap.of(
            "a", "a_value",
            "b", true,
            "c", GenericRecordImpl.create(schema.getFieldSchemas().get("c"), ImmutableMap.of(
                "c1", (short)4,
                "c2", 1.5f
            ))
        )
    );
    final JsonEncoder<GenericRecord> encoder = JsonEncoders.forSchema(schema);
    final String expected = "{\"a\":\"a_value\",\"b\":true,\"c\":{\"c1\":4,\"c2\":1.5}}";
    final String actual = encoder.encode(record).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testRecursiveRecord() {
    final SchemaBuilder builder = SchemaBuilderImpl.create(Type.RECORD).setName("LinkedList");
    final Schema schema = builder
        .setFieldSchema("head", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("tail", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
        .build();
    final JsonEncoder<GenericRecord> encoder = JsonEncoders.forSchema(schema);

    final GenericRecord record = GenericRecordImpl.create(
        schema,
        ImmutableMap.of(
            "head", "a",
            "tail", Optional.of(
                GenericRecordImpl.create(
                    schema,
                    ImmutableMap.of(
                        "head", "b",
                        "tail", Optional.<GenericRecord>empty()
                    )
                )
            )
        )
    );
    final String expected = "{\"head\":\"a\",\"tail\":{\"head\":\"b\",\"tail\":null}}";
    final String actual = encoder.encode(record).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testMutuallyRecursive() {
    final SchemaBuilder builder1 = SchemaBuilderImpl.create(Type.RECORD).setName("One");
    final SchemaBuilder builder2 = SchemaBuilderImpl.create(Type.RECORD).setName("Two");
    // TODO this slightly awkward construction is a result of the fact that we have to call
    // getPlaceholderSchema on both builders before calling build on either or they will not both be
    // properly filled. A better solution to this would be desirable, or at least calling
    // getPlaceholderSchema after build should throw an error (which would mean each builder is one
    // use).
    builder1
        .setFieldSchema("head", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("tail", OptionalSchemaImpl.create(builder2.getPlaceholderSchema()));
    builder2
        .setFieldSchema("head", PrimitiveSchemaImpl.create(Type.BOOLEAN))
        .setFieldSchema("tail", OptionalSchemaImpl.create(builder1.getPlaceholderSchema()));
    final Schema schema1 = builder1.build();
    final Schema schema2 = builder2.build();

    final JsonEncoder<GenericRecord> encoder1 = JsonEncoders.forSchema(schema1);

    final GenericRecord record = GenericRecordImpl.create(
        schema1,
        ImmutableMap.of(
            "head", "a",
            "tail", Optional.of(
                GenericRecordImpl.create(
                    schema2,
                    ImmutableMap.of(
                        "head", true,
                        "tail", Optional.of(
                            GenericRecordImpl.create(
                                schema1,
                                ImmutableMap.of(
                                    "head", "b",
                                    "tail", Optional.of(
                                        GenericRecordImpl.create(
                                            schema2,
                                            ImmutableMap.of(
                                                "head", false,
                                                "tail", Optional.empty()
                                            )
                                        )
                                    )
                                )
                            )

                        )
                    )
                )
            )
        )
    );

    final String expected = "{\"head\":\"a\",\"tail\":{\"head\":true,\"tail\":{\"head\":\"b\",\"tail\":{\"head\":false,\"tail\":null}}}}";
    final String actual = encoder1.encode(record).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testRecursiveTwice() {
    final SchemaBuilder builder = SchemaBuilderImpl.create(Type.RECORD).setName("Twice");
    final Schema schema = builder
        .setFieldSchema("head", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("tail_one", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
        .setFieldSchema("tail_two", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
        .build();
    final JsonEncoder<GenericRecord> encoder = JsonEncoders.forSchema(schema);
    final GenericRecord record = GenericRecordImpl.create(
        schema,
        ImmutableMap.of(
            "head", "a",
            "tail_one", Optional.of(
                GenericRecordImpl.create(
                    schema,
                    ImmutableMap.of(
                        "head", "b_one",
                        "tail_one", Optional.empty(),
                        "tail_two", Optional.empty()
                    )
                )
            ),
            "tail_two", Optional.of(
                GenericRecordImpl.create(
                    schema,
                    ImmutableMap.of(
                        "head", "b_two",
                        "tail_one", Optional.empty(),
                        "tail_two", Optional.empty()
                    )
                )
            )
        )
    );
    final String expected = "{\"head\":\"a\",\"tail_one\":{\"head\":\"b_one\",\"tail_one\":null,\"tail_two\":null},\"tail_two\":{\"head\":\"b_two\",\"tail_one\":null,\"tail_two\":null}}";
    final String actual = encoder.encode(record).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testUnion() {
    final Schema schema = SchemaBuilderImpl.create(Type.UNION)
        .addBranchSchema(PrimitiveSchemaImpl.create(Type.STRING))
        .addBranchSchema(PrimitiveSchemaImpl.create(Type.BOOLEAN))
        .build();
    final JsonEncoder<GenericUnion> encoder = JsonEncoders.forSchema(schema);

    {
      final GenericUnion union = GenericUnionImpl.create(schema, 0, "a_value");
      final String expected = "{\"branch_index\":0,\"value\":\"a_value\"}";
      final String actual = encoder.encode(union).toString();
      Assert.assertEquals(expected, actual);
    }
    {
      final GenericUnion union = GenericUnionImpl.create(schema, 1, true);
      final String expected = "{\"branch_index\":1,\"value\":true}";
      final String actual = encoder.encode(union).toString();
      Assert.assertEquals(expected, actual);
    }
  }

  @Test
  public void testOptional() {
    final Schema schema = OptionalSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
    final JsonEncoder<Optional<String>> encoder = JsonEncoders.forSchema(schema);
    {
      final Optional<String> present = Optional.of("a_value");
      final String expected = "\"a_value\"";
      final String actual = encoder.encode(present).toString();
      Assert.assertEquals(expected, actual);
    }
    {
      final Optional<String> present = Optional.empty();
      final String expected = "null";
      final String actual = encoder.encode(present).toString();
      Assert.assertEquals(expected, actual);
    }
  }

  @Test
  public void testExtension() {
    final Schema schema = ExtensionSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
    final JsonEncoder<GenericExtension<String>> encoder = JsonEncoders.forSchema(schema);
    final GenericExtension<String> extension = GenericExtensionImpl.create(schema, "tag", new byte[]{1, 2, 3});
    final String expected = "{\"tag\":\"tag\",\"value\":\"AQID\"}";
    final String actual = encoder.encode(extension).toString();
    Assert.assertEquals(expected, actual);
  }
}
