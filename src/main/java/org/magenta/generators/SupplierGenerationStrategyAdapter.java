package org.magenta.generators;

import java.util.List;
import java.util.function.Supplier;

import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.GenerationStrategy;
import org.magenta.commons.Preconditions;


/**
 * This class adapts an existing supplier to be used as
 * a {@link GenerationStrategy}.
 *
 * @author normand
 *
 * @param <D>
 *          The type of data being generated
 * @param <S>
 *          the data specification
 */
public class SupplierGenerationStrategyAdapter<D, S extends DataSpecification> extends AbstractGenerationStrategyAdapter<D, S> implements
    GenerationStrategy<D, S> {

  private Supplier<? extends D> strategy;

  /**
   * Default constructor.
   *
   * @param generator
   *          the simple generation strategy implementation
   * @param affectedDataSet
   *          the affected data set
   */
  public SupplierGenerationStrategyAdapter(DataKey<?> key, Supplier<? extends D> generator, List<DataKey<?>> affectedDataSet) {
    super(key, affectedDataSet);
    Preconditions.checkNotNull(generator);
    this.strategy = generator;
  }

  @Override
  protected D doGenerate(int index, Fixture<? extends S> datasets) {
    return strategy.get();
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("Supplier [").append(strategy).append("]");
    return sb.toString();
  }

}
