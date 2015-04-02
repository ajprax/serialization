package org.ajprax.serialization.io.impl;

import java.util.ServiceLoader;

import org.ajprax.serialization.io.JsonEncoder;
import org.ajprax.serialization.schema.Schema;

public interface JsonEncoderFactory {
  static final JsonEncoderFactory INSTANCE = ServiceLoader.load(JsonEncoderFactory.class).iterator().next();

  public <I> JsonEncoder<I> forSchema(Schema schema);
}
