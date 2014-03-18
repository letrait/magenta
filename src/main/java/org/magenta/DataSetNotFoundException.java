package org.magenta;

/**
 * Thrown when the requested {@link DataSet} cannot be found.
 *
 * @author ngagnon
 *
 */
public class DataSetNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * A not found dataset with a cause.
   *
   * @param message
   *          description of the error.
   * @param cause
   *          the cause of the error
   */
  public DataSetNotFoundException(String message, Throwable cause) {
    super(message, cause);

  }

  /**
   * A new exception.
   *
   * @param message
   *          the error description.
   */
  public DataSetNotFoundException(String message) {
    super(message);
  }

}
