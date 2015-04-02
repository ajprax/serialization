package org.ajprax.serialization.io.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
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
      JsonEncoder.forSchema(PrimitiveSchemaImpl.create(type));
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
      final T input
  ) {
    test(PrimitiveSchemaImpl.create(type), input);
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
    final Schema schema = ExtensionSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
    test(schema, GenericExtensionImpl.create(schema, "tag", new byte[] {1, 2, 3}));
  }

  @Test
  public void testArray() {
    test(ArraySchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING)), Lists.newArrayList("a", "b", "c"));
  }

  @Test
  public void testFixedSizeArray() {
    test(
        FixedSizeArraySchemaImpl.create(3, PrimitiveSchemaImpl.create(Type.STRING)),
        Lists.newArrayList("a", "b", "c")
    );
    // TODO failure case?
  }

  @Test
  public void testSet() {
    test(
        SetSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING)),
        Sets.newHashSet("a", "b")
    );
  }

  @Test
  public void testMap() {
    test(
        MapSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING), PrimitiveSchemaImpl.create(Type.BOOLEAN)),
        ImmutableMap.of("a", true, "b", false)
    );
    // a serialized map which does not obey the unique keys rule will not successfully loop.
  }

  @Test
  public void testUnion() {
    final Schema schema = UnionSchemaImpl.create(ImmutableList.of(PrimitiveSchemaImpl.create(Type.STRING), PrimitiveSchemaImpl.create(Type.BOOLEAN)));
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
    final Schema schema = OptionalSchemaImpl.create(PrimitiveSchemaImpl.create(Type.STRING));
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
    final SchemaBuilder builder = SchemaBuilderImpl.create(Type.RECORD).setName("LinkedList");
    final Schema schema = builder
        .setFieldSchema("head", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("tail", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
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
    final SchemaBuilder builder = SchemaBuilderImpl.create(Type.RECORD).setName("Twice");
    final Schema schema = builder
        .setFieldSchema("head", PrimitiveSchemaImpl.create(Type.STRING))
        .setFieldSchema("tail_one", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
        .setFieldSchema("tail_two", OptionalSchemaImpl.create(builder.getPlaceholderSchema()))
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
