package org.ajprax.serialization.schema.impl;

import java.util.ServiceLoader;

import org.ajprax.serialization.schema.Schema;

public interface SchemaBuilderFactory {
  static final SchemaBuilderFactory INSTANCE = ServiceLoader.load(SchemaBuilderFactory.class).iterator().next();

  Schema.Builder builder(Schema.Type type);
}
