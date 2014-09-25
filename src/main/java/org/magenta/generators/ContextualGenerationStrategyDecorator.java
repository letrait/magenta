package org.magenta.generators;

import java.util.concurrent.Callable;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.core.LoopCycleDetector;
import org.magenta.core.injection.FixtureContext;
import org.magenta.events.PreDataSetGenerated;



/**
 * Decorator of a GenerationStrategy that setups the FixtureContext.
 *
 * @author ngagnon
 *
 * @param <D> the type of data
 * @param <S> the type of data specification
 */
public class ContextualGenerationStrategyDecorator<D, S extends DataSpecification> implements GenerationStrategy<D, S> {

  private GenerationStrategy<D, S> delegate;
  private FixtureContext<S> context;
  private DataKey<D> key;


  public ContextualGenerationStrategyDecorator(GenerationStrategy<D, S> delegate, DataKey<D> key, FixtureContext<S> context) {
    this.delegate = delegate;
    this.key = key;
    this.context = new LoopCycleDetector<S>(context, key);
  }

  @Override
  public Iterable<D> generate(final int numberOfElements, final DataDomain<? extends S> fixture) {

    Iterable<D> data = this.context.execute(new Callable<Iterable<D>>(){

      @Override
      public Iterable<D> call() throws Exception {
        return delegate.generate(numberOfElements, fixture);
      }

    }, fixture);

    return data;
  }

  @Override
  public Iterable<D> generate(final DataDomain<? extends S> fixture) {

    Iterable<D> data =  this.context.execute(new Callable<Iterable<D>>(){

      @Override
      public Iterable<D> call() throws Exception {
        Iterable<D> data = delegate.generate(fixture);

        context.post(new PreDataSetGenerated(key, data, fixture));

        return data;
      }

    }, fixture);

    return data;
  }

  @Override
  public Iterable<DataKey<?>> getModifiedDataSet() {
    return delegate.getModifiedDataSet();
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append(ContextualGenerationStrategyDecorator.class.getSimpleName()).append(" delegating to [").append(this.delegate.toString()).append(']');
    return sb.toString();
  }

}