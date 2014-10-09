package org.ajprax.serialization.errors;

public final class TypeMismatchException extends RuntimeException {
  public TypeMismatchException(String msg) { super(msg); }
  public TypeMismatchException(String msg, Throwable cause) { super(msg, cause); }
}
