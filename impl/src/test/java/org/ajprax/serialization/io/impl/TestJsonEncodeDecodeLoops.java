package org.ajprax.serialization.io.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.ajprax.serialization.generic.GenericRecord;
import org.ajprax.serialization.generic.impl.GenericExtensionImpl;
import org.ajprax.serialization.generic.impl.GenericRecordImpl;
import org.ajprax.serialization.generic.impl.GenericUnionImpl;
import org.ajprax.serialization.io.JsonDecoder;
import org.ajprax.serialization.io.JsonEncoder;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.Schema.Builder;
import org.ajprax.serialization.schema.Schema.Type;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonEncodeDecodeLoops {

  private static <T> void test(
      final Schema schema,
      final T input
  ) {
    final JsonEncoder<T> encoder = JsonEncoder.forSchema(schema);
    final JsonDecoder<T> decoder = JsonDecoder.forSchema(schema);
    Assert.assertEquals(input, decoder.decode(encoder.encode(input)));
  }

  private static void testUnsupportedPrimitive(
      final Schema.Type type
  ) {
    try {
      JsonEncoder.forSchema(Schema.primitive(type));
      JsonDecoder.forSchema(Schema.primitive(type));
    } catch (UnsupportedOperationException uoe) {
      Assert.assertEquals(
          String.format("Schema type: '%s' is unsupported in Java.", type.name()),
          uoe.getMessage()
      );
    }
  }

  private static <T> void testSupportedPrimitive(
      final Schema.Type type,
      final T input
  ) {
    test(Schema.primitive(type), input);
  }

  @Test
  public void testPrimitives() {
    testUnsupportedPrimitive(Type.UNSIGNED_8);
    testUnsupportedPrimitive(Type.UNSIGNED_16);
    testUnsupportedPrimitive(Type.UNSIGNED_32);
    testUnsupportedPrimitive(Type.UNSIGNED_64);
    testUnsupportedPrimitive(Type.UNSIGNED_BIG);
    testUnsupportedPrimitive(Type.SIGNED_8);

    testSupportedPrimitive(Type.SIGNED_16, (short) 5);
    testSupportedPrimitive(Type.SIGNED_32, 5);
    testSupportedPrimitive(Type.SIGNED_64, 5l);
    testSupportedPrimitive(Type.SIGNED_BIG, BigInteger.valueOf(5l));
    testSupportedPrimitive(Type.FLOAT_32, 1.5f);
    testSupportedPrimitive(Type.FLOAT_64, 1.5);
    testSupportedPrimitive(Type.FLOAT_BIG, BigDecimal.valueOf(1.5));
    testSupportedPrimitive(Type.BOOLEAN, true);
    testSupportedPrimitive(Type.STRING, "abc");
  }

  @Test
  public void testExtension() {
    final Schema schema = Schema.builder(Type.EXTENSION).setTagSchema(Schema.primitive(Type.STRING)).build();
    test(schema, GenericExtensionImpl.create(schema, "tag", new byte[] {1, 2, 3}));
  }

  @Test
  public void testArray() {
    test(Schema.builder(Type.ARRAY).setElementSchema(Schema.primitive(Type.STRING)).build(), Lists.newArrayList("a", "b", "c"));
  }

  @Test
  public void testFixedSizeArray() {
    test(
        Schema.builder(Type.FIXED_SIZE_ARRAY).setSize(3).setElementSchema(Schema.primitive(Type.STRING)).build(),
        Lists.newArrayList("a", "b", "c")
    );
    // TODO failure case?
  }

  @Test
  public void testSet() {
    test(
        Schema.builder(Type.SET).setElementSchema(Schema.primitive(Type.STRING)).build(),
        Sets.newHashSet("a", "b")
    );
  }

  @Test
  public void testMap() {
    test(
        Schema.builder(Type.MAP).setKeySchema(Schema.primitive(Type.STRING)).setValueSchema(Schema.primitive(Type.BOOLEAN)).build(),
        ImmutableMap.of("a", true, "b", false)
    );
    // a serialized map which does not obey the unique keys rule will not successfully loop.
  }

  @Test
  public void testUnion() {
    final Schema schema = Schema.builder(Type.UNION).addBranchSchema(Schema.primitive(Type.STRING)).addBranchSchema(Schema.primitive(Type.BOOLEAN)).build();
    test(
        schema,
        GenericUnionImpl.create(schema, 0, "abc")
    );
    test(
        schema,
        GenericUnionImpl.create(schema, 1, true)
    );
  }

  @Test
  public void testOptional() {
    final Schema schema = Schema.builder(Type.OPTIONAL).setElementSchema(Schema.primitive(Type.STRING)).build();
    test(
        schema,
        Optional.of("abc")
    );
    test(
        schema,
        Optional.empty()
    );
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
    test(
        schema,
        GenericRecordImpl.create(
            schema,
            ImmutableMap.of(
                "a", "a_value",
                "b", true,
                "c", GenericRecordImpl.create(schema.getFieldSchemas().get("c"), ImmutableMap.of(
                    "c1", (short) 4,
                    "c2", 1.5f
                ))
            )
        )
    );
  }

  @Test
  public void testRecursiveRecord() {
    final Builder builder = Schema.builder(Type.RECORD).setName("LinkedList");
    final Schema schema = builder
        .setFieldSchema("head", Schema.primitive(Type.STRING))
        .setFieldSchema("tail", Schema.builder(Type.OPTIONAL).setElementSchema(builder.getPlaceholderSchema()).build())
        .build();
    test(
        schema,
        GenericRecordImpl.create(
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
        )
    );
  }

  @Test
  public void testMutuallyRecursiveRecord() {
    final Builder builder1 = Schema.builder(Type.RECORD).setName("One");
    final Builder builder2 = Schema.builder(Type.RECORD).setName("Two");
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

    test(
        schema1,
        GenericRecordImpl.create(
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
        )
    );
  }

  @Test
  public void testRecursiveTwice() {
    final Builder builder = Schema.builder(Type.RECORD).setName("Twice");
    final Schema schema = builder
        .setFieldSchema("head", Schema.primitive(Type.STRING))
        .setFieldSchema("tail_one", Schema.builder(Type.OPTIONAL).setElementSchema(builder.getPlaceholderSchema()).build())
        .setFieldSchema("tail_two", Schema.builder(Type.OPTIONAL).setElementSchema(builder.getPlaceholderSchema()).build())
        .build();
    test(
        schema,
        GenericRecordImpl.create(
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
        )
    );
  }
}
