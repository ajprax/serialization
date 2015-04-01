package org.ajprax.serialization.io;

public interface Decoder<I, O> {
  O decode(I input);
}
