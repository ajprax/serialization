package org.ajprax.serialization.io.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Preconditions;
import org.ajprax.serialization.io.JsonEncoder;
import org.ajprax.serialization.schema.Schema;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class JsonEncoders {

  public static final JsonEncoder<Integer> SIGNED_32 = IntNode::new;
  public static final JsonEncoder<Long> SIGNED_64 = LongNode::new;
  public static final JsonEncoder<BigInteger> SIGNED_BIG = BigIntegerNode::new;
  public static final JsonEncoder<Float> FLOAT_32 = FloatNode::new;
  public static final JsonEncoder<Double> FLOAT_64 = DoubleNode::new;
  public static final JsonEncoder<BigDecimal> FLOAT_BIG = DecimalNode::new;
  public static final JsonEncoder<Boolean> BOOLEAN = b -> (b) ? BooleanNode.getTrue() : BooleanNode.getFalse();
  public static final JsonEncoder<String> STRING = TextNode::new;

  // TODO enum, extension

  public static <T> JsonEncoder<List<T>> array(
      final JsonEncoder<T> tEncoder
  ) {
    return input -> {
      final ArrayNode array = JsonUtils.MAPPER.createArrayNode();
      for (final T t : input) {
        array.add(tEncoder.encode(t));
      }
      return array;
    };
  }

  public static <T> JsonEncoder<List<T>> fixedSizeArray(
      final JsonEncoder<T> tEncoder,
      final int size
  ) {
    return input -> {
      Preconditions.checkArgument(input.size() == size, "Input size: '%s' does not match fixed size: '%s'.", input.size(), size);
      final ArrayNode array = JsonUtils.MAPPER.createArrayNode();
      for (final T t : input) {
        array.add(tEncoder.encode(t));
      }
      return array;
    };
  }

  public static <T> JsonEncoder<Set<T>> set(
      final JsonEncoder<T> tEncoder
  ) {
    // TODO should sets be encoded as an object which indicates the intended unicity of each element?
    return input -> {
      final ArrayNode array = JsonUtils.MAPPER.createArrayNode();
      for (final T t : input) {
        array.add(tEncoder.encode(t));
      }
      return array;
    };
  }

  public static <K, V> JsonEncoder<Map<K, V>> map(
      final JsonEncoder<K> kEncoder,
      final JsonEncoder<V> vEncoder
  ) {
    return input -> {
      // TODO should maps be encoded as an object which indicates the intended unicity of keys?
      final ArrayNode array = JsonUtils.MAPPER.createArrayNode();
      for (final Map.Entry<K, V> entry : input.entrySet()) {
        final ObjectNode object = JsonUtils.MAPPER.createObjectNode();
        object.set("k", kEncoder.encode(entry.getKey()));
        object.set("v", vEncoder.encode(entry.getValue()));
        array.add(object);
      }
      return array;
    };
  }

  // TODO union

  public static <T> JsonEncoder<Optional<T>> optional(
      final JsonEncoder<T> tEncoder
  ) {
    return input -> input.map(tEncoder::encode).orElse(NullNode.getInstance());
  }

  // TODO record

  @SuppressWarnings("unchecked")
  private static <T> JsonEncoder<T> cast(
      final JsonEncoder<?> encoder
  ) {
    return (JsonEncoder<T>) encoder;
  }

  public static <T> JsonEncoder<T> forSchema(
      final Schema schema
  ) {
    switch (schema.getType()) {
      case UNSIGNED_8:
      case UNSIGNED_16:
      case UNSIGNED_32:
      case UNSIGNED_64:
      case UNSIGNED_BIG:
      case SIGNED_8:
      case SIGNED_16: throw new UnsupportedOperationException(String.format("Schema type: '%s' is unsupported in Java.", schema.getType()));
      case ENUM:
      case EXTENSION:
      case UNION:
      case RECORD: throw new NotImplementedException();
      case SIGNED_32: return cast(SIGNED_32);
      case SIGNED_64: return cast(SIGNED_64);
      case SIGNED_BIG: return cast(SIGNED_BIG);
      case FLOAT_32: return cast(FLOAT_32);
      case FLOAT_64: return cast(FLOAT_64);
      case FLOAT_BIG: return cast(FLOAT_BIG);
      case BOOLEAN: return cast(BOOLEAN);
      case STRING: return cast(STRING);
      case ARRAY: return cast(array(cast(forSchema(schema.getElementSchema()))));
      case FIXED_SIZE_ARRAY: return cast(fixedSizeArray(cast(forSchema(schema.getElementSchema())), schema.getSize()));
      case SET: return cast(set(cast(forSchema(schema.getElementSchema()))));
      case MAP: return cast(map(cast(forSchema(schema.getKeySchema())), cast(forSchema(schema.getValueSchema()))));
      case OPTIONAL: return cast(optional(cast(forSchema(schema.getElementSchema()))));
      default: throw new RuntimeException(String.format("Unknown schema type: '%s'", schema.getType()));
    }
  }

  private JsonEncoders() { }
}
