package org.magenta.generators;

import java.util.Set;

import org.magenta.CycleDetectedInGenerationException;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;

import com.google.inject.internal.Sets;


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
  private DataKey<D> key;

  private Set<DataDomain> generatingDataDomain;

  //TODO
  //use a map of stack using the datadomain as a key

  public NonReentrantDecorator(GenerationStrategy<D, S> delegate, DataKey<D> key) {
    this.delegate = delegate;
    this.key = key;
    this.generatingDataDomain = Sets.newHashSet();
  }

  @Override
  public Iterable<D> generate(int numberOfElements, DataDomain<? extends S> dataDomain) {
    //DataDomainManager<S> manager = (DataDomainManager<S>) datasetMap;
    //manager.pushOnGenerationCallStack(key);

    if(generatingDataDomain.contains(dataDomain)){
      throw new CycleDetectedInGenerationException("Infinite loop detected for generation of key " +key);
    }


    try {

      generatingDataDomain.add(dataDomain);

      Iterable<D> data = this.delegate.generate(numberOfElements, dataDomain);

      return data;
    } finally {
      generatingDataDomain.remove(dataDomain);
    }
  }

  @Override
  public Iterable<D> generate(DataDomain<? extends S> dataDomain) {
    //DataDomainManager<S> manager = (DataDomainManager<S>) dataDomain;
    //manager.pushOnGenerationCallStack(key);

    if(generatingDataDomain.contains(dataDomain)){
      throw new CycleDetectedInGenerationException("Infinite loop detected for generation of key " +key);
    }

    try {
      generatingDataDomain.add(dataDomain);
      Iterable<D> data = this.delegate.generate(dataDomain);
      return data;
    } finally {
      generatingDataDomain.remove(dataDomain);
    }

  }

  @Override
  public Iterable<DataKey<?>> getTriggeredGeneratedDataKeys() {
    return delegate.getTriggeredGeneratedDataKeys();
  }

}



