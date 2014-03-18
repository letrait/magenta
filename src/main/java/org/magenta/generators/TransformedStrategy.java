package org.magenta.generators;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Decorator of a {@link GenerationStrategy} that filters and transforms the output of the decorated generator.
 *
 * @author ngagnon
 *
 * @param <D> The type of data generated by this generator
 * @param <O> the type of data generated by the decorated generator
 * @param <S> The data specification type needed by the decorated strategy
 */
public class TransformedStrategy<D, O, S extends DataSpecification> implements GenerationStrategy<D, S> {

  private GenerationStrategy<? extends O, ? super S> strategy;
  private Predicate<? super O> filter;
  private Function<? super O, D> converter;

  /**
   * Default constructor.
   *
   * @param strategy the decorated strategy
   * @param filter the filter
   * @param converter a conversion function
   */
  public TransformedStrategy(GenerationStrategy<? extends O, ? super S> strategy, Predicate<? super O> filter, Function<? super O, D> converter) {
    this.strategy = strategy;
    this.converter = converter;
    this.filter = filter;
  }

  @Override
  public Iterable<D> generate(DataDomain<? extends S> dataDomain) {
    return FluentIterable.from(strategy.generate(dataDomain)).filter(filter).transform(converter);
  }

  @Override
  public Iterable<D> generate(int numberOfElements, DataDomain<? extends S> dataDomain) {
    return FluentIterable.from(strategy.generate(dataDomain)).filter(filter).transform(converter).limit(numberOfElements);
  }

  @Override
  public Iterable<DataKey<?>> getTriggeredGeneratedDataKeys() {
    return strategy.getTriggeredGeneratedDataKeys();
  }

}
