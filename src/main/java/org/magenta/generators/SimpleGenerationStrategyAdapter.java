package org.magenta.generators;

import java.util.List;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.SimpleGenerationStrategy;

import com.google.common.base.Preconditions;

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
public class SimpleGenerationStrategyAdapter<D, S extends DataSpecification> extends AbstractGenerationStrategyAdapter<D, S> implements
    GenerationStrategy<D, S> {

  private SimpleGenerationStrategy<? extends D, ? super S> strategy;

  /**
   * Default constructor.
   * @param generator the simple generation strategy implementation
   * @param affectedDataSet the affected data set
   */
  public SimpleGenerationStrategyAdapter(SimpleGenerationStrategy<? extends D, ? super S> generator, List<DataKey<?>> affectedDataSet) {
    this(generator, null, null, affectedDataSet);
  }

  /**
   * Default constructor.
   * @param generator the simple generation strategy implementation
   * @param numberOfElements the number of elements to generate.
   * @param affectedDataSet the affected data set
   */
  public SimpleGenerationStrategyAdapter(SimpleGenerationStrategy<? extends D, ? super S> generator, int numberOfElements,
      List<DataKey<?>> affectedDataSet) {
    this(generator, numberOfElements, null, affectedDataSet);
    Preconditions.checkArgument(numberOfElements >= 0, "Number of elements should be positive, illegal value : %s", numberOfElements);
  }

  private SimpleGenerationStrategyAdapter(SimpleGenerationStrategy<? extends D, ? super S> generator, Integer numberOfElements, Object extraArgs,
      List<DataKey<?>> affectedDataSet) {
    super(numberOfElements, affectedDataSet);
    Preconditions.checkNotNull(generator);

    this.strategy = generator;
  }

  @Override
  protected D doGenerate(int index, DataDomain<? extends S> datasets) {
    try {
      return strategy.generateItem(datasets);
    } catch (RuntimeException re) {
      //throw new IllegalStateException(String.format("Error while generating the element number %s", index), re);
      throw re;
    }

  }

  @Override
  protected int getPreferredNumberOfItems(S specification) {
    return strategy.getPreferredNumberOfItems(specification);
  }

}
