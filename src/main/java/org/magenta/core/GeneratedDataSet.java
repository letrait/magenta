package org.magenta.core;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.GenerationStrategy;
import org.magenta.events.PostDataSetGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.eventbus.EventBus;

/**
 * A {@linkplain DataSet} implementation that relies on a
 * {@link GenerationStrategy} to build its set of data.
 *
 * @author ngagnon
 *
 * @param <D>
 *          the type of data
 */
public class GeneratedDataSet<D, S extends DataSpecification> extends AbstractDataSet<D> {

  private static final  Logger LOG = LoggerFactory.getLogger(GeneratedDataSet.class);

  private Supplier<Iterable<D>> supplier;
  private Fixture<S> domain;
  private GenerationStrategy<D, S> strategy;
  private DataKey<D> key;
  private EventBus eventBus;
  private boolean postProcessing;


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
  public  GeneratedDataSet(final Fixture<S> domain, final GenerationStrategy<D, ? super S> strategy, final DataKey<D> key, PickStrategy picker, EventBus eventBus) {
    super(key.getType(), picker,domain.getRandomizer());
    this.strategy = (GenerationStrategy)strategy;
    this.domain = domain;
    this.key = key;
    this.eventBus = eventBus;
    this.supplier = Suppliers.memoize(new Supplier<Iterable<D>>() {
      @Override
      public Iterable<D> get() {
        LOG.trace("Generating [{}] for {}.", GeneratedDataSet.this.key, GeneratedDataSet.this);
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

    if (!postProcessing) {
      postProcessing = true;
      this.eventBus.post(new PostDataSetGenerated(this.key,this.domain));
      LOG.trace("GENERATION COMPETED for [{}]", GeneratedDataSet.this.key);
    }

    return data;
  }

  @Override
  public boolean isGenerated() {
    return true;
  }

  @Override
  public boolean isPersistent() {
    return false;
  }

  @Override
  public boolean isConstant() {
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb=new StringBuilder();
    sb.append(GeneratedDataSet.class.getSimpleName()).append(" using a [")
    .append(this.strategy.toString()).append(']');

    return sb.toString();
  }


}
