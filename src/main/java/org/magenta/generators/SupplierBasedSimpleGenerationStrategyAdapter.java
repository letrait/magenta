package org.magenta.generators;

import java.util.List;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.SimpleGenerationStrategy;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

/**
 * This class adapts an existing {@link SimpleGenerationStrategy} to be used as
 * a {@link GenerationStrategy}. If no number Of elements to generate is passed
 * in to the constructor, then the specified
 * {@link SimpleGenerationStrategy#getPreferredNumberOfItems(DataSpecification)}
 * will be used instead.
 *
 * @author normand
 *
 * @param <D>
 *          The type of data being generated
 * @param <S>
 *          the data specification
 */
public class SupplierBasedSimpleGenerationStrategyAdapter<D, S extends DataSpecification> extends AbstractGenerationStrategyAdapter<D, S> implements
    GenerationStrategy<D, S> {

  private Supplier<? extends D> strategy;

  /**
   * Default constructor.
   *
   * @param generator
   *          the simple generation strategy implementation
   * @param numberOfElements
   *          the number of elements to generate.
   * @param affectedDataSet
   *          the affected data set
   */
  public SupplierBasedSimpleGenerationStrategyAdapter(DataKey<?> key, Supplier<? extends D> generator, List<DataKey<?>> affectedDataSet) {
    super(key, affectedDataSet);
    Preconditions.checkNotNull(generator);
    this.strategy = generator;
  }

  @Override
  protected D doGenerate(int index, DataDomain<? extends S> datasets) {
    return strategy.get();
  }

}
