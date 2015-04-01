package org.ajprax.serialization.io;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonEncoder<I> extends Encoder<I, JsonNode> { }
