package org.ajprax.serialization.io;

public interface Encoder<I, O> {
  O encode(I input);
}
