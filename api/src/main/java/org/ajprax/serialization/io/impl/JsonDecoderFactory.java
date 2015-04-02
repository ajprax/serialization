package org.ajprax.serialization.io.impl;

import java.util.ServiceLoader;

import org.ajprax.serialization.io.JsonDecoder;
import org.ajprax.serialization.schema.Schema;

public interface JsonDecoderFactory {
  static final JsonDecoderFactory INSTANCE = ServiceLoader.load(JsonDecoderFactory.class).iterator().next();

  public <O> JsonDecoder<O> forSchema(Schema schema);
}
