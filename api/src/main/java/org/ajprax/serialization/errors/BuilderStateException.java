package org.ajprax.serialization.errors;

public final class BuilderStateException extends IllegalStateException {
  public BuilderStateException(String msg) { super(msg); }
  public BuilderStateException(String msg, Throwable cause) { super(msg, cause); }
}
