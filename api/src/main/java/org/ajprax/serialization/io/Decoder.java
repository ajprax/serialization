package org.ajprax.serialization.io;

public interface Decoder<O> {
  O decode(byte[] input);
}
