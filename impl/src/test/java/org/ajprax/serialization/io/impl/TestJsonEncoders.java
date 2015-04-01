package org.ajprax.serialization.io.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.ajprax.serialization.io.JsonEncoder;
import org.ajprax.serialization.schema.Schema;
import org.ajprax.serialization.schema.Schema.Type;
import org.ajprax.serialization.schema.impl.ArraySchemaImpl;
import org.ajprax.serialization.schema.impl.FixedSizeArraySchemaImpl;
import org.ajprax.serialization.schema.impl.MapSchemaImpl;
import org.ajprax.serialization.schema.impl.PrimitiveSchemaImpl;
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
}
