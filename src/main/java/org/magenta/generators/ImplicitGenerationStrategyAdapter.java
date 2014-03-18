package org.magenta.generators;

import java.util.List;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.ImplicitGenerationStrategy;

import com.google.common.collect.Iterables;


/**
 * This class adapts an existing {@link ImplicitGenerationStrategy} to be used as
 * a {@link GenerationStrategy}.
 *
 * @author normand
 *
 * @param <D>
 *          The type of data being generated
 * @param <S>
 *          the data specification
 */
public class ImplicitGenerationStrategyAdapter<D, S extends DataSpecification> implements GenerationStrategy<D, S> {

  private ImplicitGenerationStrategy<D, S> generator;
  private List<DataKey<?>> affectedDataSet;

  /**
   * @param generator the generator
   * @param affectedDataSet the affected dataset
   */
  public ImplicitGenerationStrategyAdapter(ImplicitGenerationStrategy<D, S> generator, List<DataKey<?>> affectedDataSet) {
    super();
    this.affectedDataSet = affectedDataSet;
    this.generator = generator;
  }

  @Override
  public Iterable<D> generate(int numberOfElements, DataDomain<? extends S> domain) {
    return Iterables.limit(generate(domain), numberOfElements);
  }

  @Override
  public Iterable<D> generate(DataDomain<? extends S> domain) {
    return generator.generate(domain);
  }

  @Override
  public Iterable<DataKey<?>> getTriggeredGeneratedDataKeys() {
    return affectedDataSet;
  }

}
