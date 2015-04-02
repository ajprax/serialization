package org.ajprax.serialization.generic;

public interface GenericExtension<TAG> extends GenericValue<byte[]> {
  TAG getTag();
}
