package org.magenta.generators;

import java.util.List;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;

import com.google.common.collect.Lists;

/**
 * Abstract template class for {@link GenerationStrategy} that generates only one element at a time.
 *
 * @author ngagnon
 *
 * @param <D>
 * @param <S>
 */
public abstract class AbstractGenerationStrategyAdapter<D, S extends DataSpecification> implements GenerationStrategy<D, S> {

  private final Integer numberOfElements;
  private List<DataKey<?>> affectedDataSet;

  /**
   * Default constructor.
   *
   * @param numberOfElements the number of elements to generate or null if we should use this class <code>getPreferredNumberOfItems</code>.
   * @param affectedDataSet The dataset that are affected by this generation strategy (other than the generated object).
   */
  public AbstractGenerationStrategyAdapter(Integer numberOfElements, List<DataKey<?>> affectedDataSet) {
    this.numberOfElements = numberOfElements;
    this.affectedDataSet = affectedDataSet;
  }

  /**
   * Return the preferred number of items to generate when none are specified through the constructor.
   *
   * @param specification the data domain specification
   * @return the preferred number of items
   */
  protected abstract int getPreferredNumberOfItems(S specification);

  /**
   * Generate an item at the given <code>index</code> and from the <code>dataDomain</code>.
   *
   * @param index the index in the collections being generated of the item to generate. e.g. "6" would mean "generating the 6th objects".
   * @param dataDomain the data domain
   * @return a generated item of type <code>D</code>
   */
  protected abstract D doGenerate(int index, DataDomain<? extends S> dataDomain);

  @Override
  public Iterable<D> generate(DataDomain<? extends S> dataDomain) {

    List<D> result = Lists.newArrayList();

    int s = numberOfElements == null ? getPreferredNumberOfItems(dataDomain.getSpecification()) : numberOfElements;

    for (int i = 0; i < s; i++) {
      result.add(doGenerate(i, dataDomain));
    }
    return result;
  }

  @Override
  public Iterable<D> generate(int numberOfElements, DataDomain<? extends S> repo) {
    List<D> result = Lists.newArrayList();
    for (int i = 0; i < numberOfElements; i++) {
      result.add(doGenerate(i, repo));
    }

    return result;
  }

  @Override
  public Iterable<DataKey<?>> getTriggeredGeneratedDataKeys() {
    return affectedDataSet;
  }

}
