package org.ajprax.serialization.io.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.ajprax.serialization.generic.GenericExtension;
import org.ajprax.serialization.generic.GenericRecord;
import org.ajprax.serialization.generic.GenericUnion;
import org.ajprax.serialization.io.JsonEncoder;
import org.ajprax.serialization.schema.Schema;

public class JsonEncoders {

  // TODO look into how to improve the state of generics in this class.
  // TODO ensure that values which do not match a given schema get a useful error message.

  public static final class JsonEncoderImplFactory implements JsonEncoderFactory {

    @Override
    public <T> JsonEncoder<T> forSchema(final Schema schema) {
      return cast(JsonEncoders.forSchema(schema, Maps.newHashMap()));
    }
  }

  /**
   * Placeholder for a record encoder which may encode recursive records. Must be filled with a
   * concrete record encoder before it can be used.
   */
  private static final class PlaceholderJsonEncoder implements JsonEncoder<Object> {

    private JsonEncoder<Object> mDelegate = null;

    private void fill(
        final JsonEncoder<Object> delegate
    ) {
      mDelegate = delegate;
    }

    @Override
    public JsonNode encode(
        final Object input
    ) {
      Preconditions.checkState(
          null != mDelegate,
          "Cannot use a PlaceholderJsonEncoder which has not been filled."
      );
      return mDelegate.encode(input);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> JsonEncoder<T> cast(
      final JsonEncoder<?> encoder
  ) {
    return (JsonEncoder<T>) encoder;
  }

  private static final JsonEncoder<Short> SIGNED_16 = ShortNode::new;
  private static final JsonEncoder<Integer> SIGNED_32 = IntNode::new;
  private static final JsonEncoder<Long> SIGNED_64 = LongNode::new;
  private static final JsonEncoder<BigInteger> SIGNED_BIG = BigIntegerNode::new;
  private static final JsonEncoder<Float> FLOAT_32 = FloatNode::new;
  private static final JsonEncoder<Double> FLOAT_64 = DoubleNode::new;
  private static final JsonEncoder<BigDecimal> FLOAT_BIG = DecimalNode::new;
  private static final JsonEncoder<Boolean> BOOLEAN =
      b -> (b) ? BooleanNode.getTrue() : BooleanNode.getFalse();
  private static final JsonEncoder<String> STRING = TextNode::new;

  private static <T extends Enum<T>> JsonEncoder<T> enumm(
      final String enumName
  ) {
    return input -> {
      final ObjectNode obj = JsonUtils.MAPPER.createObjectNode();
      obj.put("name", new TextNode(enumName));
      obj.put("value", new TextNode(input.name()));
      return obj;
    };
  }

  private static <TAG> JsonEncoder<GenericExtension<TAG>> extension(
      final JsonEncoder<TAG> tagEncoder
  ) {
    return input -> {
      final ObjectNode obj = JsonUtils.MAPPER.createObjectNode();
      obj.put("tag", tagEncoder.encode(input.getTag()));
      obj.put("value", new BinaryNode(input.getValue()));
      return obj;
    };
  }

  private static <T> JsonEncoder<List<T>> array(
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

  private static <T> JsonEncoder<List<T>> fixedSizeArray(
      final JsonEncoder<T> tEncoder,
      final int size
  ) {
    return input -> {
      Preconditions.checkArgument(
          input.size() == size,
          "Input size: '%s' does not match fixed size: '%s'.",
          input.size(),
          size
      );
      final ArrayNode array = JsonUtils.MAPPER.createArrayNode();
      for (final T t : input) {
        array.add(tEncoder.encode(t));
      }
      return array;
    };
  }

  private static <T> JsonEncoder<Set<T>> set(
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

  private static <K, V> JsonEncoder<Map<K, V>> map(
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

  private static JsonEncoder<GenericUnion> union(
      final List<JsonEncoder<Object>> branchEncoders
  ) {
    return input -> {
      final ObjectNode obj = JsonUtils.MAPPER.createObjectNode();
      obj.put("branch_index", input.getBranchIndex());
      obj.put("value", branchEncoders.get(input.getBranchIndex()).encode(input.getValue()));
      return obj;
    };
  }

  private static <T> JsonEncoder<Optional<T>> optional(
      final JsonEncoder<T> tEncoder
  ) {
    // TODO should null input be considered the same as Optional.empty?
    return input -> input.map(tEncoder::encode).orElse(NullNode.getInstance());
  }

  private static JsonEncoder<GenericRecord> record(
      final ImmutableMap<String, JsonEncoder<Object>> fieldEncoders
  ) {
    return input -> {
      final ObjectNode obj = JsonUtils.MAPPER.createObjectNode();
      for (String field : input.getSchema().getFieldSchemas().keySet()) {
        obj.put(field, fieldEncoders.get(field).encode(input.get(field)));
      }
      return obj;
    };
  }

  /**
   * Recursively creates encoders for a given Schema. If the same record is encountered twice in a
   * Schema tree, breaks recursion to prevent an infinite loop.
   *
   * TODO clean up implementation.
   *
   * @param schema Schema for which to builder an encoder.
   * @param knownSchemas Schemas which have already been seen during the creation of this encoder.
   * @return A JsonEncoder for the given Schema.
   */
  private static JsonEncoder<Object> forSchema(
      final Schema schema,
      final Map<Schema, JsonEncoder<Object>> knownSchemas
  ) {
    final JsonEncoder<Object> knownEncoder = knownSchemas.get(schema);
    if (knownEncoder != null) {
      return knownEncoder;
    } else {
      switch (schema.getType()) {
        case UNSIGNED_8:
        case UNSIGNED_16:
        case UNSIGNED_32:
        case UNSIGNED_64:
        case UNSIGNED_BIG:
        case SIGNED_8: throw new UnsupportedOperationException(
            String.format("Schema type: '%s' is unsupported in Java.", schema.getType())
        );
        case SIGNED_16: {
          knownSchemas.put(schema, cast(SIGNED_16));
          return cast(SIGNED_16);
        }
        case SIGNED_32: {
          knownSchemas.put(schema, cast(SIGNED_32));
          return cast(SIGNED_32);
        }
        case SIGNED_64: {
          knownSchemas.put(schema, cast(SIGNED_64));
          return cast(SIGNED_64);
        }
        case SIGNED_BIG: {
          knownSchemas.put(schema, cast(SIGNED_BIG));
          return cast(SIGNED_BIG);
        }
        case FLOAT_32: {
          knownSchemas.put(schema, cast(FLOAT_32));
          return cast(FLOAT_32);
        }
        case FLOAT_64: {
          knownSchemas.put(schema, cast(FLOAT_64));
          return cast(FLOAT_64);
        }
        case FLOAT_BIG: {
          knownSchemas.put(schema, cast(FLOAT_BIG));
          return cast(FLOAT_BIG);
        }
        case BOOLEAN: {
          knownSchemas.put(schema, cast(BOOLEAN));
          return cast(BOOLEAN);
        }
        case STRING: {
          knownSchemas.put(schema, cast(STRING));
          return cast(STRING);
        }
        case ENUM: {
          final JsonEncoder<Object> encoder = cast(enumm(schema.getName()));
          knownSchemas.put(schema, encoder);
          return encoder;
        }
        case EXTENSION: {
          final JsonEncoder<Object> tagEncoder = forSchema(schema.getTagSchema(), knownSchemas);
          final JsonEncoder<Object> encoder = cast(extension(tagEncoder));
          knownSchemas.put(schema, encoder);
          return encoder;
        }
        case ARRAY: {
          final JsonEncoder<?> encoder = array(forSchema(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, cast(encoder));
          return cast(encoder);
        }
        case FIXED_SIZE_ARRAY: {
          final JsonEncoder<?> encoder = fixedSizeArray(
              forSchema(schema.getElementSchema(), knownSchemas),
              schema.getSize()
          );
          knownSchemas.put(schema, cast(encoder));
          return cast(encoder);
        }
        case SET: {
          final JsonEncoder<?> encoder = set(forSchema(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, cast(encoder));
          return cast(encoder);
        }
        case MAP: {
          final JsonEncoder<?> encoder = map(
              forSchema(schema.getKeySchema(), knownSchemas),
              forSchema(schema.getValueSchema(), knownSchemas)
          );
          knownSchemas.put(schema, cast(encoder));
          return cast(encoder);
        }
        case UNION: {
          final List<JsonEncoder<Object>> branchEncoders = Lists.transform(
              schema.getBranchSchemas(),
              branchSchema -> forSchema(branchSchema, knownSchemas)
          );
          final JsonEncoder<Object> encoder = cast(union(branchEncoders));
          knownSchemas.put(schema, encoder);
          return encoder;
        }
        case OPTIONAL: {
          final JsonEncoder<?> encoder = optional(
              forSchema(schema.getElementSchema(), knownSchemas)
          );
          knownSchemas.put(schema, cast(encoder));
          return cast(encoder);
        }
        case RECORD: {
          final PlaceholderJsonEncoder placeholder = new PlaceholderJsonEncoder();
          knownSchemas.put(schema, placeholder);
          final Map<String, JsonEncoder<Object>> fieldEncoders = Maps.transformValues(
              schema.getFieldSchemas(),
              fieldSchema -> forSchema(fieldSchema, knownSchemas)
          );
          final JsonEncoder<GenericRecord> encoder = record(ImmutableMap.copyOf(fieldEncoders));
          placeholder.fill(cast(encoder));
          return placeholder;
        }
        default: throw new RuntimeException(String.format("Unknown schema type: '%s'", schema.getType()));
      }
    }
  }

  private JsonEncoders() { }
}
