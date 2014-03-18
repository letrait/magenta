package org.magenta.testing;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;
import org.magenta.DataSet;

/**
 * Base class for DataSet assertions.
 *
 * @author ngagnon
 *
 * @param <A> the type of assert
 * @param <D> the type of data set
 */
public abstract class AbstractDataSetAssert<A extends AbstractDataSetAssert<A, D>, D> extends AbstractAssert<A, DataSet<D>> {
  /**
   * Default constructor.
   *
   * @param actual the dataset being asserted.
   * @param type the type of this assert.
   */
  protected AbstractDataSetAssert(DataSet<D> actual, Class<?> type) {
    super(actual, type);
  }

  /**
   * Assert that the {@code actual} contains only the specified {@code values} in any order.
   *
   * @param values the values
   * @return this assert
   */
  @SafeVarargs
  public final A containsOnly(D... values) {
    Assertions.assertThat(actual.list()).containsOnly(values);
    return myself;
  }

  /**
   * Assert that the {@code actual} contains exactly the specified {@code values} in the specified order..
   *
   * @param values the values
   * @return this assert
   */
  @SafeVarargs
  public final A containsExactly(D... values) {
    Assertions.assertThat(actual.list()).containsExactly(values);
    return myself;
  }
}
