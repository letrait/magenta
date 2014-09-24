package org.magenta.core;

import org.magenta.CycleDetectedInGenerationException;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * A {@linkplain DataSet} implementation that relies on a
 * {@link GenerationStrategy} to build its set of data.
 *
 * @author ngagnon
 *
 * @param <D>
 *          the type of data
 */
public class GeneratedDataSet<D> extends AbstractDataSet<D> {

  private static final  Logger LOG = LoggerFactory.getLogger(GeneratedDataSet.class);

  private Supplier<Iterable<D>> supplier;
  private DataDomain<?> domain;
  private GenerationStrategy<D, ?> strategy;
  private boolean relationProcessed;

  /**
   * Default constructor.
   *
   * @param domain
   *          the domain that the strategy will use to generate data
   * @param strategy
   *          the generation strategy
   * @param type
   *          the type of data
   * @param <S>
   *          the data specification type
   */
  public <S extends DataSpecification> GeneratedDataSet(final DataDomain<S> domain, final GenerationStrategy<D, ? super S> strategy, final Class<D> type) {
    super(type, domain.getRandomizer());
    this.strategy = strategy;
    this.domain = domain;
    this.supplier = Suppliers.memoize(new Supplier<Iterable<D>>() {
      @Override
      public Iterable<D> get() {
        LOG.trace("Generating data for {}.", GeneratedDataSet.this);
        Iterable<D> result = strategy.generate(domain);
        return result;
      }
    });
  }

  @Override
  public Iterable<D> get() {
    Iterable<D> data = this.supplier.get();

    //This has to be called after the supplier is initialized, since the loading of the triggered data key may
    //indirectly call this dataset again, in which case the initialized data will be returned and the relationProcessed
    //flag will be true
    if (!relationProcessed) {
      relationProcessed = true;
      if (this.strategy.getTriggeredGeneratedDataKeys() != null) {
        for (DataKey<?> k : this.strategy.getTriggeredGeneratedDataKeys()) {
          try{
          this.domain.dataset(k)
              .get();
          }catch(CycleDetectedInGenerationException cdge){
            //ignore
            //the key is already being loaded elsewhere
          }
        }
      }
    }
    return data;
  }

  @Override
  public boolean isGenerated() {
    return true;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("type", getType()).add("domain", domain.getName()).add("strategy", strategy).toString();
  }

}
