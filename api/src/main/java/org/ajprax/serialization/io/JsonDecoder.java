package org.ajprax.serialization.io;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonDecoder<O> extends Decoder<JsonNode, O> { }
