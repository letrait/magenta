package org.magenta.core;

import org.magenta.random.RandomBuilder;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * A generic dataset implementation.
 *
 * @author ngagnon
 *
 * @param <D>
 *          the type of data
 */
public class GenericDataSet<D> extends AbstractDataSet<D> {

  private final Supplier<? extends Iterable<D>> delegate;

  /**
   * Constructor that wraps an Iterable as data.
   *
   * @param type
   *          the type of data
   * @param data
   *          the data
   * @param random
   *          the random
   */
  public GenericDataSet(Iterable<D> data, Class<D> type, RandomBuilder random) {
    super(type, random);
    this.delegate = Suppliers.ofInstance(data);
  }

  /**
   * Constructor that use a delegate as data supplier.
   *
   * @param type
   *          the type of data
   * @param delegate
   *          the data supplier
   * @param random
   *          the random
   *
   */
  public GenericDataSet(Supplier<? extends Iterable<D>> delegate, Class<D> type, RandomBuilder random) {
    super(type, random);
    this.delegate = delegate;
  }

  @Override
  public Iterable<D> get() {
    return delegate.get();
  }

  @Override
  public boolean isGenerated() {
    return false;
  }

  @Override
  public boolean isPersistent() {
    return false;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("type", getType()).toString();
  }

}
