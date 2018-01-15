package org.magenta.generators;

import java.util.List;

import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
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

  private final DataKey<?> key;
  private List<DataKey<?>> affectedDataSet;

  /**
   * Default constructor.
   * @param affectedDataSet The dataset that are affected by this generation strategy (other than the generated object).
   */
  public AbstractGenerationStrategyAdapter(DataKey<?> key, List<DataKey<?>> affectedDataSet) {
    this.key = key;
    this.affectedDataSet = affectedDataSet;
  }

  /**
   * Generate an item at the given <code>index</code> and from the <code>dataDomain</code>.
   *
   * @param index the index in the collections being generated of the item to generate. e.g. "6" would mean "generating the 6th objects".
   * @param dataDomain the data domain
   * @return a generated item of type <code>D</code>
   */
  protected abstract D doGenerate(int index, Fixture<? extends S> dataDomain);

  @Override
  public Iterable<D> generate(Fixture<? extends S> fixture) {

    List<D> result = Lists.newArrayList();

    int s = fixture.sizeOf(key);

    for (int i = 0; i < s; i++) {
      result.add(doGenerate(i, fixture));
    }

    return result;
  }

  @Override
  public Iterable<D> generate(int numberOfElements, Fixture<? extends S> repo) {
    List<D> result = Lists.newArrayList();
    for (int i = 0; i < numberOfElements; i++) {
      result.add(doGenerate(i, repo));
    }

    return result;
  }

  @Override
  public Iterable<DataKey<?>> getModifiedDataSet() {
    return affectedDataSet;
  }


}
