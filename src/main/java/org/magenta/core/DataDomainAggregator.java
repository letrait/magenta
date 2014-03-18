package org.magenta.core;

import java.util.Set;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSetNotFoundException;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.Generator;
import org.magenta.GeneratorNotFoundException;
import org.magenta.random.Randoms;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * Aggregator that links an existing data domain with another.
 *
 * @author normand
 *
 * @param <S>
 */
public class DataDomainAggregator<S extends DataSpecification> implements DataDomain<S> {

  private final DataDomain<S> parent;

  private final DataDomain<? super S> delegate;

  /**
   * Default constructor.
   *
   * @param delegate
   *          the delegate
   * @param parent
   *          the parent
   */
  public DataDomainAggregator(DataDomain<? super S> delegate, DataDomain<S> parent) {
    this.parent = parent;
    this.delegate = delegate;
  }

  @Override
  public <D> DataSet<D> dataset(Class<D> clazz) {
    Preconditions.checkNotNull(clazz);
    try {
      return delegate.dataset(clazz);
    } catch (DataSetNotFoundException dsnfe) {
      return parent.dataset(clazz);
    }
  }

  @Override
  public <D> DataSet<D> dataset(DataKey<D> key) {
    Preconditions.checkNotNull(key);
    try {
      return delegate.dataset(key);
    } catch (DataSetNotFoundException dsnfe) {
      return parent.dataset(key);
    }
  }

  @Override
  public Set<DataKey<?>> datasetKeys() {
    return parent == null ? delegate.datasetKeys() : FluentIterable.from(Iterables.concat(delegate.datasetKeys(), parent.datasetKeys())).toSet();
  }

  @Override
  public Iterable<DataSet<?>> datasets() {
    return parent == null ? delegate.datasets() : FluentIterable.from(Iterables.concat(delegate.datasets(), parent.datasets()));
  }

  @Override
  public <D> Generator<D> generator(Class<D> clazz) {
    Preconditions.checkNotNull(clazz);
    try {
      return delegate.generator(clazz);
    } catch (GeneratorNotFoundException dsnfe) {
      return parent.generator(clazz);
    }
  }

  @Override
  public <D> Generator<D> generator(DataKey<D> key) {
    Preconditions.checkNotNull(key);
    try {
      return delegate.generator(key);
    } catch (GeneratorNotFoundException dsnfe) {
      return parent.generator(key);
    }
  }

  @Override
  public Set<DataKey<?>> strategyKeys() {
    return parent == null ? delegate.strategyKeys() : FluentIterable.from(Iterables.concat(delegate.strategyKeys(), parent.strategyKeys())).toSet();
  }

  @Override
  public Iterable<GenerationStrategy<?, ? extends DataSpecification>> strategies() {
    return parent == null ? delegate.strategies() : FluentIterable.from(Iterables.concat(delegate.strategies(), parent.strategies()));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <D> GenerationStrategy<D, S> strategy(DataKey<D> key) {
    Preconditions.checkNotNull(key);

    GenerationStrategy<D, ? super S> gen = delegate.strategy(key);

    if (gen == null && getParent() != null) {
      return getParent().strategy(key);
    }

    return (GenerationStrategy<D, S>) gen;
  }

  @Override
  public <D> GenerationStrategy<D, S> strategy(Class<D> clazz) {
    return strategy(DataKey.makeDefault(clazz));
  }

  @Override
  public DataDomain<S> getParent() {
    return parent;
  }

  @Override
  public Randoms getRandomizer() {
    return delegate.getRandomizer();
  }

  @Override
  public S getSpecification() {
    return parent.getSpecification();
  }

  @Override
  public String getName() {
    return "aggregation of " + delegate.getName() + " and " + parent.getName();
  }

}
