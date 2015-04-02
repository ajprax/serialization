package org.ajprax.serialization.io;

import com.fasterxml.jackson.databind.JsonNode;
import org.ajprax.serialization.io.impl.JsonEncoderFactory;
import org.ajprax.serialization.schema.Schema;

public interface JsonEncoder<I> extends Encoder<I, JsonNode> {
  static <T> JsonEncoder<T> forSchema(
      final Schema schema
  ) {
    return JsonEncoderFactory.INSTANCE.forSchema(schema);
  }
}
