package org.ajprax.serialization.io.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.ajprax.serialization.generic.GenericExtension;
import org.ajprax.serialization.generic.GenericRecord;
import org.ajprax.serialization.generic.GenericUnion;
import org.ajprax.serialization.generic.impl.GenericExtensionImpl;
import org.ajprax.serialization.generic.impl.GenericRecordImpl;
import org.ajprax.serialization.io.JsonDecoder;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.Schema.Type;
import org.ajprax.serialization.schema.SchemaBuilder;
import org.ajprax.serialization.schema.impl.ArraySchemaImpl;
import org.ajprax.serialization.schema.impl.ExtensionSchemaImpl;
import org.ajprax.serialization.schema.impl.FixedSizeArraySchemaImpl;
import org.ajprax.serialization.schema.impl.MapSchemaImpl;
import org.ajprax.serialization.schema.impl.OptionalSchemaImpl;
import org.ajprax.serialization.schema.impl.PrimitiveSchemaImpl;
import org.ajprax.serialization.schema.impl.SchemaBuilderImpl;
import org.ajprax.serialization.schema.impl.SetSchemaImpl;
import org.ajprax.serialization.schema.impl.UnionSchemaImpl;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonDecoders {

  private static void testUnsupportedPrimitive(
      final Schema.Type type
  ) {
    try {
      JsonDecoder.forSchema(PrimitiveSchemaImpl.create(type));
    } catch (UnsupportedOperationException uoe) {
      Assert.assertEquals(
          String.format("Schema type: '%s' is unsupported in Java.", type.name()),
          uoe.getMessage()
      );
    }
  }

  private static <T> void testSupportedPrimitive(
      final Schema.Type type,
      final JsonNode input,
      final T expected
  ) {
    final JsonDecoder<T> decoder = JsonDecoder.forSchema(PrimitiveSchemaImpl.create(type));
    final T actual = decoder.decode(input);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testPrimitives() {
    testUnsupportedPrimitive(Type.UNSIGNED_8);
    testUnsupportedPrimitive(Type.UNSIGNED_16);
    testUnsupportedPrimitive(Type.UNSIGNED_32);
    testUnsupportedPrimitive(Type.UNSIGNED_64);
    testUnsupportedPrimitive(Type.UNSIGNED_BIG);
    testUnsupportedPrimitive(Type.SIGNED_8);

    testSupportedPrimitive(Type.SIGNED_16, new ShortNode((short) 5), (short) 5);
    testSupportedPrimitive(Type.SIGNED_32, new IntNode(5), 5);
    testSupportedPrimitive(Type.SIGNED_64, new LongNode(5), 5l);
    testSupportedPrimitive(Type.SIGNED_BIG, new BigIntegerNode(BigInteger.valueOf(5l)), BigInteger.valueOf(5l));
    testSupportedPrimitive(Type.FLOAT_32, new FloatNode(1.5f), 1.5f);
    testSupportedPrimitive(Type.FLOAT_64, new DoubleNode(1.5), 1.5);
    testSupportedPrimitive(Type.FLOAT_BIG, new DecimalNode(BigDecimal.valueOf(1.5)), BigDecimal.valueOf(1.5));
    testSupportedPrimitive(Type.BOOLEAN, BooleanNode.getTrue(), true);
    testSupportedPrimitive(Type.STRING, new TextNode("abc"), "abc");
  }

  @Test
  public void testExtension() {
    final Schema schema = ExtensionSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
    final JsonDecoder<GenericExtension<String>> decoder = JsonDecoder.forSchema(schema);
    final ObjectNode input = JsonUtils.MAPPER.createObjectNode();
    input.put("tag", "tag");
    input.put("value", "AQID");
    final GenericExtension<String> expected = GenericExtensionImpl.create(schema, "tag", new byte[] {1, 2, 3});
    final GenericExtension<String> actual = decoder.decode(input);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testArray() {
    final Schema schema = ArraySchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
    final JsonDecoder<List<String>> decoder = JsonDecoder.forSchema(schema);
    final ArrayNode input = JsonUtils.MAPPER.createArrayNode().add("a").add("b").add("c");
    final List<String> expected = Lists.newArrayList("a", "b", "c");
    final List<String> actual = decoder.decode(input);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testFixedSizeArray() {
    final Schema schema = FixedSizeArraySchemaImpl.create(3, PrimitiveSchemaImpl.create(Type.STRING));
    final JsonDecoder<List<String>> decoder = JsonDecoder.forSchema(schema);

    {
      final ArrayNode input = JsonUtils.MAPPER.createArrayNode().add("a").add("b").add("c");
      final List<String> expected = Lists.newArrayList("a", "b", "c");
      final List<String> actual = decoder.decode(input);
      Assert.assertEquals(expected, actual);
    }

    {
      final ArrayNode badInput = JsonUtils.MAPPER.createArrayNode().add("a");
      try {
        decoder.decode(badInput);
      } catch (IllegalArgumentException iae) {
        Assert.assertNull(iae.getMessage()); // This is a very weak check.
      }
    }
  }

  @Test
  public void testSet() {
    final Schema schema = SetSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
    final JsonDecoder<Set<String>> decoder = JsonDecoder.forSchema(schema);
    {
      final ArrayNode input = JsonUtils.MAPPER.createArrayNode().add("a").add("b");
      final Set<String> expected = Sets.newHashSet("a", "b");
      final Set<String> actual = decoder.decode(input);
      Assert.assertEquals(expected, actual);
    }
    {
      final ArrayNode input = JsonUtils.MAPPER.createArrayNode().add("a").add("b").add("a");
      final Set<String> expected = Sets.newHashSet("a", "b");
      final Set<String> actual = decoder.decode(input);
      Assert.assertEquals(expected, actual);
    }
  }

  private static ObjectNode kv(
      String key,
      Boolean value
  ) {
    final ObjectNode obj = JsonUtils.MAPPER.createObjectNode();
    obj.put("k", key);
    obj.put("v", value);
    return obj;
  }

  @Test
  public void testMap() {
    final Schema schema = MapSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING), PrimitiveSchemaImpl.create(Type.BOOLEAN));
    final JsonDecoder<Map<String, Boolean>> decoder = JsonDecoder.forSchema(schema);
    {
      final ArrayNode input = JsonUtils.MAPPER.createArrayNode().add(kv("a", true)).add(kv("b", false));
      final Map<String, Boolean> expected = ImmutableMap.of("a", true, "b", false);
      final Map<String, Boolean> actual = decoder.decode(input);
      Assert.assertEquals(expected, actual);
    }
    {
      final ArrayNode input = JsonUtils.MAPPER.createArrayNode().add(kv("a", true)).add(kv("a", false));
      final Map<String, Boolean> expected = ImmutableMap.of("a", false);
      final Map<String, Boolean> actual = decoder.decode(input);
      Assert.assertEquals(expected, actual);
    }
  }

  @Test
  public void testUnion() {
    final Schema schema = UnionSchemaImpl.create(ImmutableList.of(PrimitiveSchemaImpl.create(Type.STRING), PrimitiveSchemaImpl.create(Type.BOOLEAN)));
    final JsonDecoder<GenericUnion> decoder = JsonDecoder.forSchema(schema);
    {
      final ObjectNode input = JsonUtils.MAPPER.createObjectNode();
      input.put("branch_index", 0);
      input.put("value", "abc");
      final GenericUnion decoded = decoder.decode(input);
      Assert.assertEquals(0, decoded.getBranchIndex());
      final String expected = "abc";
      final String actual = decoded.getTypedValue();
      Assert.assertEquals(expected, actual);
    }
    {
      final ObjectNode input = JsonUtils.MAPPER.createObjectNode();
      input.put("branch_index", 1);
      input.put("value", true);
      final GenericUnion decoded = decoder.decode(input);
      Assert.assertEquals(1, decoded.getBranchIndex());
      final Boolean actual = decoded.getTypedValue();
      Assert.assertTrue(actual);
    }
  }

  @Test
  public void testOptional() {
    final Schema schema = OptionalSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
    final JsonDecoder<Optional<String>> decoder = JsonDecoder.forSchema(schema);
    {
      final JsonNode input = new TextNode("abc");
      final Optional<String> expected = Optional.of("abc");
      final Optional<String> actual = decoder.decode(input);
      Assert.assertEquals(expected, actual);
    }
    {
      final JsonNode input = NullNode.getInstance();
      final Optional<String> expected = Optional.empty();
      final Optional<String> actual = decoder.decode(input);
      Assert.assertEquals(expected, actual);
    }
  }

  @Test
  public void testRecord() throws IOException {
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
    final JsonDecoder<GenericRecord> decoder = JsonDecoder.forSchema(schema);
    final JsonNode input = JsonUtils.MAPPER.readTree("{\"a\":\"a_value\",\"b\":true,\"c\":{\"c1\":4,\"c2\":1.5}}");
    final GenericRecord expected = GenericRecordImpl.create(
        schema,
        ImmutableMap.of(
            "a", "a_value",
            "b", true,
            "c", GenericRecordImpl.create(schema.getFieldSchemas().get("c"), ImmutableMap.of(
                "c1", (short) 4,
                "c2", 1.5f
            ))
        )
    );
    final GenericRecord actual = decoder.decode(input);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testRecursiveRecord() throws IOException {
    final SchemaBuilder builder = SchemaBuilderImpl.create(Type.RECORD).setName("LinkedList");
    final Schema schema = builder
        .setFieldSchema("head", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("tail", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
        .build();
    final JsonDecoder<GenericRecord> decoder = JsonDecoder.forSchema(schema);
    final JsonNode input = JsonUtils.MAPPER.readTree("{\"head\":\"a\",\"tail\":{\"head\":\"b\",\"tail\":null}}");
    final GenericRecord expected = GenericRecordImpl.create(
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
    final GenericRecord actual = decoder.decode(input);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testMutuallyRecursiveRecords() throws IOException {
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

    final JsonDecoder<GenericRecord> decoder1 = JsonDecoder.forSchema(schema1);

    final JsonNode input = JsonUtils.MAPPER.readTree("{\"head\":\"a\",\"tail\":{\"head\":true,\"tail\":{\"head\":\"b\",\"tail\":{\"head\":false,\"tail\":null}}}}");
    final GenericRecord expected = GenericRecordImpl.create(
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
    final GenericRecord actual = decoder1.decode(input);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testRecursiveTwice() throws IOException {
    final SchemaBuilder builder = SchemaBuilderImpl.create(Type.RECORD).setName("Twice");
    final Schema schema = builder
        .setFieldSchema("head", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("tail_one", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
        .setFieldSchema("tail_two", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
        .build();
    final JsonDecoder<GenericRecord> decoder = JsonDecoder.forSchema(schema);
    final JsonNode input = JsonUtils.MAPPER.readTree("{\"head\":\"a\",\"tail_one\":{\"head\":\"b_one\",\"tail_one\":null,\"tail_two\":null},\"tail_two\":{\"head\":\"b_two\",\"tail_one\":null,\"tail_two\":null}}");
    final GenericRecord expected = GenericRecordImpl.create(
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
    final GenericRecord actual = decoder.decode(input);
    Assert.assertEquals(expected, actual);
  }
}
