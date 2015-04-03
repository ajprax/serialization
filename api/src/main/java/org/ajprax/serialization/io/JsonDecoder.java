package org.ajprax.serialization.io;

import com.fasterxml.jackson.databind.JsonNode;
import org.ajprax.serialization.io.impl.JsonDecoderFactory;
import org.ajprax.serialization.schema.Schema;

public interface JsonDecoder<T> extends Decoder<JsonNode, T> {
  static <T> JsonDecoder<T> forSchema(
      final Schema schema
  ) {
    return JsonDecoderFactory.INSTANCE.forSchema(schema);
  }
}
