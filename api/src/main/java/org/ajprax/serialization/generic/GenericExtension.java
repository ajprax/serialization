package org.ajprax.serialization.generic;

public interface GenericExtension<TAG, VALUE> extends GenericValue<VALUE> {
  TAG getTag();
}
