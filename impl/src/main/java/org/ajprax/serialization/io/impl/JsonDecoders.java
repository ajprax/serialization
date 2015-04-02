package org.ajprax.serialization.io.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.ajprax.serialization.generic.GenericExtension;
import org.ajprax.serialization.generic.GenericRecord;
import org.ajprax.serialization.generic.GenericUnion;
import org.ajprax.serialization.generic.impl.GenericExtensionImpl;
import org.ajprax.serialization.generic.impl.GenericRecordImpl;
import org.ajprax.serialization.generic.impl.GenericUnionImpl;
import org.ajprax.serialization.io.JsonDecoder;
import org.ajprax.serialization.schema.Schema;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class JsonDecoders {

  // TODO look into how to improve the state of generics in this class.
  // TODO ensure that values which do not match a given schema get a useful error message.

  // TODO many of these decoders will return values instead of type errors
  // (e.g. TextNode("hi").longValue() == 0)

  public static final class JsonDecoderImplFactory implements JsonDecoderFactory {

    @Override
    public <T> JsonDecoder<T> forSchema(final Schema schema) {
      return cast(JsonDecoders.forSchema(schema, Maps.newHashMap()));
    }
  }

  private static final class PlaceholderJsonDecoder implements JsonDecoder<Object> {

    private JsonDecoder<Object> mDelegate = null;

    private void fill(
        final JsonDecoder<Object> delegate
    ) {
      mDelegate = delegate;
    }

    @Override
    public Object decode(
        final JsonNode input
    ) {
      Preconditions.checkState(
          null != mDelegate,
          "Cannot use a PlaceholderJsonDecoder which has not been filled."
      );
      return mDelegate.decode(input);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> JsonDecoder<T> cast(
      final JsonDecoder<?> decoder
  ) {
    return (JsonDecoder<T>) decoder;
  }

  private static final JsonDecoder<Short> SIGNED_16 = JsonNode::shortValue;
  private static final JsonDecoder<Integer> SIGNED_32 = JsonNode::intValue;
  private static final JsonDecoder<Long> SIGNED_64 = JsonNode::longValue;
  private static final JsonDecoder<BigInteger> SIGNED_BIG = JsonNode::bigIntegerValue;
  private static final JsonDecoder<Float> FLOAT_32 = JsonNode::floatValue;
  private static final JsonDecoder<Double> FLOAT_64 = JsonNode::doubleValue;
  private static final JsonDecoder<BigDecimal> FLOAT_BIG = JsonNode::decimalValue;
  private static final JsonDecoder<Boolean> BOOLEAN = JsonNode::booleanValue;
  private static final JsonDecoder<String> STRING = JsonNode::textValue;

  // TODO enum

  private static <TAG> JsonDecoder<GenericExtension<TAG>> extension(
      final Schema schema,
      final JsonDecoder<TAG> tagDecoder
  ) {
    return input -> {
      Preconditions.checkArgument(input.isObject());
      final JsonNode tagNode = input.get("tag");
      final JsonNode valueNode = input.get("value");
      final TAG tag = tagDecoder.decode(tagNode);
      final byte[] value;
      if (valueNode instanceof BinaryNode) {
        try {
          value = valueNode.binaryValue();
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }
      } else {
        // this is a hack based on knowledge of BinaryNode's implementation. Surely there's a better
        // way to handle serializing a binary node then deserializing it.
        value = Base64Variants.getDefaultVariant().decode(valueNode.asText());
      }
      return GenericExtensionImpl.create(schema, tag, value);
    };
  }

  private static <T> JsonDecoder<List<T>> array(
      final JsonDecoder<T> tDecoder
  ) {
    return input -> {
      Preconditions.checkArgument(input.isArray());
      final List<T> array = Lists.newArrayList();
      input.elements().forEachRemaining(elem -> array.add(tDecoder.decode(elem)));
      return array;
    };
  }

  private static <T> JsonDecoder<List<T>> fixedSizeArray(
      final int size,
      final JsonDecoder<T> tDecoder
  ) {
    return input -> {
      Preconditions.checkArgument(input.isArray());
      Preconditions.checkArgument(input.size() == size);
      final List<T> array = Lists.newArrayList();
      input.elements().forEachRemaining(elem -> array.add(tDecoder.decode(elem)));
      return array;
    };
  }

  private static <T> JsonDecoder<Set<T>> set(
      final JsonDecoder<T> tDecoder
  ) {
    return input -> {
      Preconditions.checkArgument(input.isArray());
      final Set<T> set = Sets.newHashSet();
      input.elements().forEachRemaining(elem -> set.add(tDecoder.decode(elem)));
      return set;
    };
  }

  private static <K, V> JsonDecoder<Map<K, V>> map(
      final JsonDecoder<K> kDecoder,
      final JsonDecoder<V> vDecoder
  ) {
    return input -> {
      Preconditions.checkArgument(input.isArray());
      final Map<K, V> map = Maps.newHashMap();
      input.elements().forEachRemaining(elem -> {
        final K key = kDecoder.decode(elem.get("k"));
        final V value = vDecoder.decode(elem.get("v"));
        map.put(key, value);
      });
      return map;
    };
  }

  private static JsonDecoder<GenericUnion> union(
      final Schema schema,
      final List<JsonDecoder<Object>> branchDecoders
  ) {
    return input -> {
      Preconditions.checkArgument(input.isObject());
      final int branchIndex = input.get("branch_index").intValue();
      final Object value = branchDecoders.get(branchIndex).decode(input.get("value"));
      return GenericUnionImpl.create(schema, branchIndex, value);
    };
  }

  private static <T> JsonDecoder<Optional<T>> optional(
      final JsonDecoder<T> tDecoder
  ) {
    return input -> (input.isNull()) ? Optional.empty() : Optional.of(tDecoder.decode(input));
  }

  private static JsonDecoder<GenericRecord> record(
      final Schema schema,
      final ImmutableMap<String, JsonDecoder<Object>> fieldDecoders
  ) {
    return input -> {
      Preconditions.checkArgument(input.isObject());
      final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
      for (String field : schema.getFieldSchemas().keySet()) {
        builder.put(field, fieldDecoders.get(field).decode(input.get(field)));
      }
      return GenericRecordImpl.create(schema, builder.build());
    };
  }

  private static JsonDecoder<Object> forSchema(
      final Schema schema,
      final Map<Schema, JsonDecoder<Object>> knownSchemas
  ) {
    final JsonDecoder<Object> knownDecoder = knownSchemas.get(schema);
    if (null != knownDecoder) {
      return knownDecoder;
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
        // TODO decoding into an Enum requires an enum class. Could find the class by name with reflection.
        case ENUM: throw new NotImplementedException();
        case EXTENSION: {
          final JsonDecoder<Object> tagDecoder = forSchema(schema.getTagSchema(), knownSchemas);
          final JsonDecoder<Object> decoder = cast(extension(schema, tagDecoder));
          knownSchemas.put(schema, decoder);
          return decoder;
        }
        case ARRAY: {
          final JsonDecoder<?> decoder = array(forSchema(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, cast(decoder));
          return cast(decoder);
        }
        case FIXED_SIZE_ARRAY: {
          final JsonDecoder<?> decoder = fixedSizeArray(
              schema.getSize(),
              forSchema(schema.getElementSchema(), knownSchemas)
          );
          knownSchemas.put(schema, cast(decoder));
          return cast(decoder);
        }
        case SET: {
          final JsonDecoder<?> decoder = set(forSchema(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, cast(decoder));
          return cast(decoder);
        }
        case MAP: {
          final JsonDecoder<?> decoder = map(
              forSchema(schema.getKeySchema(), knownSchemas),
              forSchema(schema.getValueSchema(), knownSchemas)
          );
          knownSchemas.put(schema, cast(decoder));
          return cast(decoder);
        }
        case UNION: {
          final List<JsonDecoder<Object>> branchDecoders = Lists.transform(
              schema.getBranchSchemas(),
              branchSchema -> forSchema(branchSchema, knownSchemas)
          );
          final JsonDecoder<Object> decoder = cast(union(schema, branchDecoders));
          knownSchemas.put(schema, decoder);
          return decoder;
        }
        case OPTIONAL: {
          final JsonDecoder<?> decoder =optional(forSchema(schema.getElementSchema(), knownSchemas));
          knownSchemas.put(schema, cast(decoder));
          return cast(decoder);
        }
        case RECORD: {
          final PlaceholderJsonDecoder placeholder = new PlaceholderJsonDecoder();
          knownSchemas.put(schema, placeholder);
          final Map<String, JsonDecoder<Object>> fieldDecoders = Maps.transformValues(
              schema.getFieldSchemas(),
              fieldSchema -> forSchema(fieldSchema, knownSchemas)
          );
          final JsonDecoder<GenericRecord> decoder = record(schema, ImmutableMap.copyOf(fieldDecoders));
          placeholder.fill(cast(decoder));
          return placeholder;
        }
        default: throw new RuntimeException(String.format("Unknown schema type: '%s'", schema.getType()));
      }
    }
  }

  private JsonDecoders() { }
}
