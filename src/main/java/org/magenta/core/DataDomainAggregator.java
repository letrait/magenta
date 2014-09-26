package org.magenta.core;

import java.util.Set;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSetNotFoundException;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.GenerationStrategy;
import org.magenta.Generator;
import org.magenta.GeneratorNotFoundException;
import org.magenta.random.FluentRandom;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.EventBus;

/**
 * Aggregator that links an existing data domain with another.
 *
 * @author normand
 *
 * @param <S>
 */
public class DataDomainAggregator<S extends DataSpecification> implements Fixture<S> {

  private final Fixture<S> parent;

  private final Fixture<? super S> delegate;

  /**
   * Default constructor.
   *
   * @param delegate
   *          the delegate
   * @param parent
   *          the parent
   */
  public DataDomainAggregator(Fixture<? super S> delegate, Fixture<S> parent) {
    this.parent = parent;
    this.delegate = delegate;
    this.delegate.getEventBus().register(parent);
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
  public Fixture<S> getParent() {
    return parent;
  }

  @Override
  public FluentRandom getRandomizer() {
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

  @Override
  public int getVersion(){
    return this.parent.getVersion() + this.delegate.getVersion();
  }

  @Override
  public Integer numberOfElementsFor(DataKey<?> key) {
    return delegate.numberOfElementsFor(key);
  }

  @Override
  public EventBus getEventBus() {
    return this.parent.getEventBus();
  }

  @Override
  public Integer numberOfElementsFor(Class<?> clazz) {
    return delegate.numberOfElementsFor(clazz);
  }

}
