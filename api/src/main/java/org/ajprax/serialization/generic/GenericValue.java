package org.ajprax.serialization.generic;

import org.ajprax.serialization.schema.Schema;

public interface GenericValue<VALUE> {
  Schema getSchema();

  VALUE getValue();
}
