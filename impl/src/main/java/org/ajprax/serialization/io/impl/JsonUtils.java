package org.ajprax.serialization.io.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  public static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * Pretty print a JsonNode.
   *
   * @param node JsonNode to pretty print.
   * @return A String containing the pretty printed input Json.
   */
  public static String prettyPrint(
      final JsonNode node
  ) {
    try {
      return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    } catch (JsonProcessingException jpe) {
      throw new RuntimeException(jpe);
    }
  }

  private JsonUtils() {}
}
