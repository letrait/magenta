package org.magenta;

/**
 * A generator provides the same functionality as a {@link DataSet} but it output newly generated data each time.
 *
 * @author ngagnon
 *
 * @param <T>
 *          the type of data being generated
 */
public interface Generator<T> extends DataSet<T> {


  /**
   * Restrict the data domain of this generator to the specified items. This
   * will replace any {@link DataSet} of the same type currently defined in the
   * {@link Fixture}. The override will only be visible for that generator as it is
   * using a new child {@link Fixture} node.
   *
   * @param o
   *          an array of object, {@link DataSet} or collection.
   * @return a new generator
   */
  public Generator<T> restrictTo(Object... o);
}
