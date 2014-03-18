package org.magenta;

/**
 * Thrown when a requested {@link Generator} is not found.
 *
 * @author ngagnon
 *
 */
public class GeneratorNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * A not found dataset with a cause.
   *
   * @param message
   *          description of the error.
   * @param cause
   *          the cause of the error
   */
  public GeneratorNotFoundException(String message, Throwable cause) {
    super(message, cause);

  }

  /**
   * A new exception.
   *
   * @param message
   *          the error description.
   */
  public GeneratorNotFoundException(String message) {
    super(message);
  }

}
