package org.ajprax.serialization.io;

import java.util.ServiceLoader;

import com.fasterxml.jackson.databind.JsonNode;
import org.ajprax.serialization.schema.Schema;

public interface JsonEncoder<I> extends Encoder<I, JsonNode> {
  static final Provider PROVIDER = ServiceLoader.load(Provider.class).iterator().next();

  static <T> JsonEncoder<T> forSchema(
      final Schema schema
  ) {
    return PROVIDER.forSchema(schema);
  }

  /**
   * Provider interface which allows JsonEncoder implementations to be created from the API package
   * via service loading.
   *
   * Users should not override this class.
   */
  interface Provider {
    <T> JsonEncoder<T> forSchema(Schema schema);
  }
}
