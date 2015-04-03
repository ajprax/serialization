package org.ajprax.serialization.io;

public interface Decoder<U, T> {
  T decode(U input);
}
