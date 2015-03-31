package org.ajprax.serialization.io;

public interface Encoder<I> {
  byte[] encode(I input);
}
