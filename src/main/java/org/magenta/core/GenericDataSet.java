package org.magenta.core;

import java.util.Objects;
import java.util.function.Supplier;

import org.magenta.random.FluentRandom;

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
  public GenericDataSet(Iterable<D> data, Class<D> type, FluentRandom random) {
    super(type, random);
    this.delegate = () -> data;
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
  public GenericDataSet(Supplier<? extends Iterable<D>> delegate, Class<D> type, FluentRandom random) {
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
  public boolean isConstant() {
    return true;
  }


}
