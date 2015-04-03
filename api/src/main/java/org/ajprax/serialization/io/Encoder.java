package org.ajprax.serialization.io;

public interface Encoder<T, U> {
  U encode(T input);
}
