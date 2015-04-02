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
import org.junit.Assert;
import org.junit.Test;

public class TestJsonEncoders {
  @Test
  public void testMap() {
    final Schema schema = Schema.builder(Type.MAP)
        .setKeySchema(Schema.primitive(Type.STRING))
        .setValueSchema(Schema.primitive(Type.SIGNED_32))
        .build();

    final Map<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);
    final JsonEncoder<Map<String, Integer>> encoder = JsonEncoder.forSchema(schema);
    final String expected = "[{\"k\":\"a\",\"v\":1},{\"k\":\"b\",\"v\":2}]";
    final String actual = encoder.encode(map).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testNestedArrays() {
    final Schema schema = Schema.builder(Type.ARRAY)
        .setElementSchema(
            Schema.builder(Type.ARRAY)
                .setElementSchema(Schema.primitive(Type.STRING))
                .build()
        )
        .build();
    final List<List<String>> nested = Lists.newArrayList(Lists.newArrayList(), Lists.newArrayList("a"), Lists.newArrayList("a", "b"));
    final JsonEncoder<List<List<String>>> encoder = JsonEncoder.forSchema(schema);
    final String expected = "[[],[\"a\"],[\"a\",\"b\"]]";
    final String actual = encoder.encode(nested).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testFixedSizeArrays() {
    final Schema fixedSizeSchema = Schema.builder(Type.FIXED_SIZE_ARRAY).setSize(2).setElementSchema(Schema.primitive(Type.STRING)).build();
    final JsonEncoder<List<String>> encoder = JsonEncoder.forSchema(fixedSizeSchema);

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
    final Schema arraySchema = Schema.builder(Type.ARRAY).setElementSchema(Schema.primitive(Type.STRING)).build();
    final List<Integer> list = Lists.newArrayList(1, 2);
    final JsonEncoder<List<Integer>> encoder = JsonEncoder.forSchema(arraySchema);
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
    final Schema schema = Schema.builder(Type.ENUM).setName(Weekdays.class.getName()).setEnumSymbols(values).build();
    final JsonEncoder<Weekdays> encoder = JsonEncoder.forSchema(schema);
    final String expected = String.format("{\"name\":\"%s\",\"value\":\"MONDAY\"}", Weekdays.class.getName());
    final String actual = encoder.encode(Weekdays.MONDAY).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testRecord() {
    final Schema schema = Schema.builder(Type.RECORD)
        .setName("outer")
        .setFieldSchema("a", Schema.primitive(Type.STRING))
        .setFieldSchema("b", Schema.primitive(Type.BOOLEAN))
        .setFieldSchema("c", Schema.builder(Type.RECORD)
                .setName("inner")
                .setFieldSchema("c1", Schema.primitive(Type.SIGNED_16))
                .setFieldSchema("c2", Schema.primitive(Type.FLOAT_32))
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
    final JsonEncoder<GenericRecord> encoder = JsonEncoder.forSchema(schema);
    final String expected = "{\"a\":\"a_value\",\"b\":true,\"c\":{\"c1\":4,\"c2\":1.5}}";
    final String actual = encoder.encode(record).toString();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testRecursiveRecord() {
    final Schema.Builder builder = Schema.builder(Type.RECORD).setName("LinkedList");
    final Schema schema = builder
        .setFieldSchema("head", Schema.primitive(Type.STRING))
        .setFieldSchema("tail", Schema.builder(Type.OPTIONAL).setElementSchema(builder.getPlaceholderSchema()).build())
        .build();
    final JsonEncoder<GenericRecord> encoder = JsonEncoder.forSchema(schema);

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
    final Schema.Builder builder1 = Schema.builder(Type.RECORD).setName("One");
    final Schema.Builder builder2 = Schema.builder(Type.RECORD).setName("Two");
    // TODO this slightly awkward construction is a result of the fact that we have to call
    // getPlaceholderSchema on both builders before calling build on either or they will not both be
    // properly filled. A better solution to this would be desirable, or at least calling
    // getPlaceholderSchema after build should throw an error (which would mean each builder is one
    // use).
    builder1
        .setFieldSchema("head", Schema.primitive(Type.STRING))
        .setFieldSchema("tail", Schema.builder(Type.OPTIONAL).setElementSchema(builder2.getPlaceholderSchema()).build());
    builder2
        .setFieldSchema("head", Schema.primitive(Type.BOOLEAN))
        .setFieldSchema("tail", Schema.builder(Type.OPTIONAL).setElementSchema(builder1.getPlaceholderSchema()).build());
    final Schema schema1 = builder1.build();
    final Schema schema2 = builder2.build();

    final JsonEncoder<GenericRecord> encoder1 = JsonEncoder.forSchema(schema1);

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
    final Schema.Builder builder = Schema.builder(Type.RECORD).setName("Twice");
    final Schema schema = builder
        .setFieldSchema("head", Schema.primitive(Type.STRING))
        .setFieldSchema("tail_one", Schema.builder(Type.OPTIONAL).setElementSchema(builder.getPlaceholderSchema()).build())
        .setFieldSchema("tail_two", Schema.builder(Type.OPTIONAL).setElementSchema(builder.getPlaceholderSchema()).build())
        .build();
    final JsonEncoder<GenericRecord> encoder = JsonEncoder.forSchema(schema);
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
    final Schema schema = Schema.builder(Type.UNION)
        .addBranchSchema(Schema.primitive(Type.STRING))
        .addBranchSchema(Schema.primitive(Type.BOOLEAN))
        .build();
    final JsonEncoder<GenericUnion> encoder = JsonEncoder.forSchema(schema);

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
    final Schema schema = Schema.builder(Type.OPTIONAL).setElementSchema(Schema.primitive(Type.STRING)).build();
    final JsonEncoder<Optional<String>> encoder = JsonEncoder.forSchema(schema);
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
    final Schema schema = Schema.builder(Type.EXTENSION).setTagSchema(Schema.primitive(Type.STRING)).build();
    final JsonEncoder<GenericExtension<String>> encoder = JsonEncoder.forSchema(schema);
    final GenericExtension<String> extension = GenericExtensionImpl.create(schema, "tag", new byte[]{1, 2, 3});
    final String expected = "{\"tag\":\"tag\",\"value\":\"AQID\"}";
    final String actual = encoder.encode(extension).toString();
    Assert.assertEquals(expected, actual);
  }
}
