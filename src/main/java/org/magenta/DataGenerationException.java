package org.magenta;

public class DataGenerationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * A not found dataset with a cause.
   *
   * @param message
   *          description of the error.
   * @param cause
   *          the cause of the error
   */
  public DataGenerationException(String message, Throwable cause) {
    super(message, cause);

  }

  /**
   * A new exception.
   *
   * @param message
   *          the error description.
   */
  public DataGenerationException(String message) {
    super(message);
  }
}
