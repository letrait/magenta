package org.magenta;

public class UnboundedDataSetException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * A not found dataset with a cause.
   *
   * @param message
   *          description of the error.
   * @param cause
   *          the cause of the error
   */
  public UnboundedDataSetException(String message, Throwable cause) {
    super(message, cause);

  }

  /**
   * A new exception.
   *
   * @param message
   *          the error description.
   */
  public UnboundedDataSetException(String message) {
    super(message);
  }

}
