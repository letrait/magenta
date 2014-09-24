package org.magenta.generators;

import java.util.concurrent.Callable;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.core.LoopCycleDetector;
import org.magenta.core.injection.FixtureContext;



/**
 * Decorator of a GenerationStrategy that validate that there are no cycle in its
 * delegate data generation strategy.
 *
 * @author ngagnon
 *
 * @param <D> the type of data
 * @param <S> the type of data specification
 */
public class NonReentrantDecorator<D, S extends DataSpecification> implements GenerationStrategy<D, S> {

  private GenerationStrategy<D, S> delegate;
  private FixtureContext<S> context;


  public NonReentrantDecorator(GenerationStrategy<D, S> delegate, DataKey<D> key, FixtureContext<S> context) {
    this.delegate = delegate;
    this.context = new LoopCycleDetector<S>(context, key);
  }

  @Override
  public Iterable<D> generate(final int numberOfElements, final DataDomain<? extends S> fixture) {

    return this.context.execute(new Callable<Iterable<D>>(){

      @Override
      public Iterable<D> call() throws Exception {
        return delegate.generate(numberOfElements, fixture);
      }

    }, fixture);
  }

  @Override
  public Iterable<D> generate(final DataDomain<? extends S> fixture) {

    return this.context.execute(new Callable<Iterable<D>>(){

      @Override
      public Iterable<D> call() throws Exception {
        return delegate.generate(fixture);
      }

    }, fixture);
  }

  @Override
  public Iterable<DataKey<?>> getTriggeredGeneratedDataKeys() {
    return delegate.getTriggeredGeneratedDataKeys();
  }

}



