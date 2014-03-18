package org.magenta.generators;

import java.util.Collections;
import java.util.List;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;

import com.google.common.base.Supplier;

/**
 * This class adapts an existing {@link Supplier} to be used as
 * a {@link GenerationStrategy}.
 *
 * @author normand
 *
 * @param <D>
 *          The type of data being generated
 */
public class SupplierBasedSimpleGenerationStrategyAdapter<D> extends AbstractGenerationStrategyAdapter<D, DataSpecification> {

  private Supplier<D> generator;

  /**
   * Default number of items to generate will be read from {@link DataSpecification#getDefaultNumberOfItems()}.
   *
   * @param generator the generator
   */
  public SupplierBasedSimpleGenerationStrategyAdapter(Supplier<D> generator) {
    super(null, Collections.<DataKey<?>> emptyList());
    this.generator = generator;
  }

  /**
   * Default number of items to generate will be read from {@link DataSpecification#getDefaultNumberOfItems()}.
   *
   * @param generator the generator
   * @param affectedDataSets the affected data sets
   */
  public SupplierBasedSimpleGenerationStrategyAdapter(Supplier<D> generator, List<DataKey<?>> affectedDataSets) {
    super(null, affectedDataSets);
    this.generator = generator;
  }

  /**
   * Constructor where the number of elements to generate is specified.
   *
   * @param generator the generator
   * @param numberOfElements the number of elements to generate
   */
  public SupplierBasedSimpleGenerationStrategyAdapter(Supplier<D> generator, Integer numberOfElements) {
    super(numberOfElements, Collections.<DataKey<?>> emptyList());
    this.generator = generator;
  }

  /**
   * Constructor where the number of elements to generate is specified.
   *
   * @param generator the generator
   * @param numberOfElements the number of elements
   * @param affectedDataSets the affected data sets
   */
  public SupplierBasedSimpleGenerationStrategyAdapter(Supplier<D> generator, Integer numberOfElements, List<DataKey<?>> affectedDataSets) {
    super(numberOfElements, affectedDataSets);
    this.generator = generator;
  }

  @Override
  protected int getPreferredNumberOfItems(DataSpecification specification) {
    return specification.getDefaultNumberOfItems();
  }

  @Override
  protected D doGenerate(int i, DataDomain<? extends DataSpecification> dataDomain) {
    return generator.get();
  }

}
