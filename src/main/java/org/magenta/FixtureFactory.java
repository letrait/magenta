package org.magenta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import org.magenta.core.DataDomainAggregator;
import org.magenta.core.DataSetRelationLoader;
import org.magenta.core.EmptyDataSet;
import org.magenta.core.GeneratedDataSet;
import org.magenta.core.GeneratorImpl;
import org.magenta.core.GenericDataSet;
import org.magenta.core.PersistentDataSet;
import org.magenta.core.RestrictionHelper;
import org.magenta.core.injection.DataSetFieldHandler;
import org.magenta.core.injection.DataSpecificationFieldHandler;
import org.magenta.core.injection.FieldInjectionChainProcessor;
import org.magenta.core.injection.FieldInjectionHandler;
import org.magenta.core.injection.FluentRandomFieldHandler;
import org.magenta.core.injection.HiearchicalFieldsFinder;
import org.magenta.core.injection.Injector;
import org.magenta.core.injection.ThreadLocalDataDomainSupplier;
import org.magenta.events.DataSetRegistered;
import org.magenta.events.DataSetRemoved;
import org.magenta.generators.ContextualGenerationStrategyDecorator;
import org.magenta.generators.DataSetAggregationStrategy;
import org.magenta.generators.GeneratorAnnotationHelper;
import org.magenta.generators.IterableSupplierGenerationStrategyAdapter;
import org.magenta.generators.SupplierGenerationStrategyAdapter;
import org.magenta.generators.TransformedStrategy;
import org.magenta.random.FluentRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * This class allow the management of {@link DataSet}. Use this class to add,
 * get and remove {@link DataSet}s.
 *
 * @author normand
 *
 */
public class FixtureFactory<S extends DataSpecification> implements Fixture<S> {

  private static final  Logger LOG = LoggerFactory.getLogger(FixtureFactory.class);

  private final Fixture<S> parent;

  private boolean persistent;
  private DataStoreProvider dataStoreProvider;

  private final FluentRandom randomizer;

  private final S specification;

  private final Map<DataKey<?>, DataSet<?>> dataSetMap;
  private final Map<DataKey<?>, GenerationStrategy<?, ? extends DataSpecification>> generatorMap;
  private final Map<DataKey<?>, Integer> numberOfItemsMap;

  private final List<Processor<S>> processors;

  private final Injector injector;

  private final ThreadLocalDataDomainSupplier<S> currentFixtureSupplier;

  private EventBus eventBus;

  private int version = 1;


  private final String name;

  // -------------------------------------------------------------------------------------------------------------------------------------------------
  //
  // Constructor
  //
  // -------------------------------------------------------------------------------------------------------------------------------------------------

  /**
   * Full constructor.
   *
   * @param parent
   *          the parent to fallabck to when a requested dataset is not found.
   * @param datastoreProvider
   *          the datasource provider for persitent DataSet.
   */
  private FixtureFactory(String name, Fixture<S> parent, S specification, boolean persistent, FluentRandom randomizer, DataStoreProvider datastoreProvider,
      ThreadLocalDataDomainSupplier<S> fixtureSupplier) {
    this.name = name;
    this.parent = parent;
    this.specification = specification;
    this.randomizer = randomizer;
    this.dataStoreProvider = datastoreProvider;
    this.persistent = persistent;
    this.currentFixtureSupplier = fixtureSupplier;
    this.eventBus = new EventBus(name);


    this.dataSetMap = new HashMap<DataKey<?>, DataSet<?>>();
    this.generatorMap = new HashMap<DataKey<?>, GenerationStrategy<?, ?>>();
    this.numberOfItemsMap = new HashMap<DataKey<?>, Integer>();
    this.processors = Lists.newArrayList();

    final List<FieldInjectionHandler<S>> handlers = Lists.newArrayList();
    handlers.add(new DataSetFieldHandler());
    handlers.add(new DataSpecificationFieldHandler());
    handlers.add(new FluentRandomFieldHandler());


    this.injector = new FieldInjectionChainProcessor<S>(currentFixtureSupplier, HiearchicalFieldsFinder.SINGLETON, handlers);
  }

  // -------------------------------------------------------------------------------------------------------------------------------------------------
  //
  // Factory Methods
  //
  // -------------------------------------------------------------------------------------------------------------------------------------------------

  /**
   * Create a new root {@link FixtureFactory}.
   *
   * @param name
   *          the name of this data domain
   * @param specification
   *          the {@link DataSpecification} of this data domain
   * @param randomizer
   *          the randomizer to use
   * @param <S>
   *          the type of {@link DataSpecification}
   * @return a new {@link FixtureFactory}
   */
  public static <S extends DataSpecification> FixtureFactory<S> newRoot(String name, S specification, FluentRandom randomizer) {
    FixtureFactory<S> fixtureBuilder = new FixtureFactory<S>(name, null, specification, false, randomizer, null, new ThreadLocalDataDomainSupplier());

    fixtureBuilder.getEventBus().register(new DataSetRelationLoader());

    return fixtureBuilder;
  }

  /**
   * Open a new scope using this {@link FixtureFactory} as parent. The new
   * scope is itself a {@link FixtureFactory} where new {@link DataSet} may
   * be added that won't be visible for this {@link FixtureFactory}.
   *
   * @param name
   *          the name of the new node.
   * @return a new {@link FixtureFactory} child of this one.
   */
  public FixtureFactory<S> newNode(String name) {
    FixtureFactory<S> child = new FixtureFactory<S>(name, this, this.getSpecification(), this.persistent, this.randomizer, this.dataStoreProvider, this.currentFixtureSupplier/*,this.generationCallStack*/);

    child.getEventBus().register(this);

    return child;
  }

  /**
   * Open a new scope using this {@link FixtureFactory} as parent. The new
   * scope is itself a {@link FixtureFactory} where new {@link DataSet} may
   * be added that won't be visible for this {@link FixtureFactory}.
   *
   * @param name
   *          the new of the new node
   * @param dataspecification
   *          the {@link DataSpecification} of the new node.
   * @param <X>
   *          the type of the {@link DataSpecification} inheriting from <S>
   * @return a new DataSetManager child of this one.
   */
  @SuppressWarnings("unchecked")
  public <X extends S> FixtureFactory<X> newNode(String name, X dataspecification) {
    // Cast is safe here, because the parent will be in fact used with its child
    // DataSpecification which extends the parent data specification
    FixtureFactory<X> child = new FixtureFactory<X>(name, (FixtureFactory<X>) this, dataspecification, this.persistent, this.randomizer, this.dataStoreProvider, (ThreadLocalDataDomainSupplier<X>)this.currentFixtureSupplier/*,
       this.generationCallStack*/);

    child.getEventBus().register(this);

    return child;
  }

  /**
   * Open a new scope delegating to <code>dataDomain</code> and using this
   * {@link FixtureFactory} as parent. The new scope is itself a
   * {@link FixtureFactory} where new {@link DataSet} may be added that won't
   * be visible for this {@link FixtureFactory}.
   *
   * @param dataDomain
   *          the delegated data domain
   * @return a new DataSetManager child of this one.
   */
  public FixtureFactory<S> newNode(Fixture<? super S> dataDomain) {
    DataDomainAggregator<S> aggregation = new DataDomainAggregator<S>(dataDomain, this);
    FixtureFactory<S> child = new FixtureFactory<S>("child of " + this.getName(), aggregation, this.getSpecification(), this.persistent, randomizer, this.dataStoreProvider, this.currentFixtureSupplier/*,
         this.generationCallStack*/);

    child.getEventBus().register(this);

    return child;
  }

  public FixtureFactory<S> setDataStoreProvider(DataStoreProvider provider, boolean enablePersistence) {
   this.dataStoreProvider = provider;
   this.persistent = enablePersistence;
   return this;
  }

  // -------------------------------------------------------------------------------------------------------------------------------------------------
  //
  // Public Methods
  //
  // -------------------------------------------------------------------------------------------------------------------------------------------------

  @Override
  public S getSpecification() {
    return specification;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getVersion(){
    return version;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.expedia.gps.fixtures.DataSets#getReferenceFor(java.lang.Class)
   */

  /**
   * Return the actual parent of this DataSetMapManager.
   *
   * @return the parent or null if this is the root parent.
   */
  @Override
  public Fixture<S> getParent() {
    return parent;
  }

  public boolean isTransient() {

    return !persistent;
  }

  public boolean isPersistent() {

    return persistent;
  }

  public FixtureFactory<S> persistent(boolean persistent){
    this.persistent = persistent;
    return this;
  }

  /**
   * return the {@link DataStoreProvider}.
   *
   * @return the {@link DataStoreProvider}
   */
  public DataStoreProvider getDataStoreProvider() {
    return dataStoreProvider;
  }

  /**
   *
   * @return the {@link Random}
   */
  @Override
  public FluentRandom getRandomizer() {
    return this.randomizer;
  }

  /**
   * <p>
   * Convenient method used to replace existing data set. By "restricting", we
   * mean replacing an existing dataset by a fixed one having only one or two
   * elements.
   * </p>
   * <p>
   * The purpose of doing this is to restrain the range of a certain type of
   * data so every other dataset depending on it for their generation are forced
   * to use your sample.
   * </p>
   * <p>
   * Let say we have a dataset of "City" containing dozens of cities. This
   * dataset is used by a generation strategy to generate "Monument". Each
   * "Monument" generated within one of the available cities. If we want to
   * generate "Monument" for the city of Paris only, then we need to "restrict"
   * the dataset of Cities to only one element being "Paris".
   * </p>
   *
   * Here is a sample of code:
   *
   * <pre>
   * FixtureFactory tourismDomain = TourismDomain.createDomain();
   * City paris = CityBuilder.build(&quot;Paris&quot;);
   * Monument monument = tourismDomain.restrictTo(paris).dataset(Monument.class).any();
   *
   * Assert.assertEquals(paris, monument.getCity());
   *
   * </pre>
   * @param first the first object of the array
   * @param rest
   *          an array of object from which the dataset will be created
   * @return this FixturesManager
   */
  public FixtureFactory<S> restrictTo(Object first,Object... rest) {

    FixtureFactory<S> child = newNode("restricted " + this.getName());

    RestrictionHelper.applyRestrictions(child, first, rest);

    return child;
  }

  /**
   * Return a new {@link FixtureFactory} node which return fixed values for
   * every {@link DataSet} identified by <code>classes</code>. Normally,
   * generated data set are regenerated for new node but this method "fixes" the
   * generated values as they are currently found in this
   * {@link FixtureFactory}.
   *
   * @param classes
   *          the dataset default identifiers.
   * @return a new {@link FixtureFactory} node.
   */
  public FixtureFactory<S> fix(Class<?>... classes) {

    DataKey<?>[] keys = FluentIterable.from(Arrays.asList(classes)).transform(DataKey.classToKey()).toArray(DataKey.class);

    return fix(keys);
  }

  /**
   * Return a new {@link FixtureFactory} node which return fixed values for
   * every {@link DataSet} identified by <code>keys</code>. Normally, generated
   * data set are regenerated for new node but this method "fixes" the generated
   * values as they are currently found in this {@link FixtureFactory}.
   *
   * @param keys
   *          the data set identifiers.
   * @return a new {@link FixtureFactory} node.
   */
  @SuppressWarnings("unchecked")
  public FixtureFactory<S> fix(DataKey<?>... keys) {
    FixtureFactory<S> child = newNode("frozen " + this.getName());
    for (DataKey<?> key : keys) {
      @SuppressWarnings("rawtypes")
      DataSet ds = this.dataset(key);
      if (ds.isGenerated()) {
        // this trick replace all generated dataset by fixed one which will
        // prevent regeneration of data
        child.newDataSet(key).composedOf(ds);
      }
    }
    return child;
  }

  // -------------------------------------------------------------------------------------------------------------------------------------------------
  //
  // Builder methods
  //
  // -------------------------------------------------------------------------------------------------------------------------------------------------

  /**
   * Return a builder allowing you to create a new {@link DataSet} for the
   * <code>key</code> in this {@link FixtureFactory}.
   *
   * @param key
   *          the qualifier of the {@link DataSet} to create.
   * @param <D>
   *          the type of data
   * @return a new builder
   */
  public <D> DataSetBuilder<D, D> newDataSet(DataKey<D> key) {
    return new DataSetBuilder<D, D>(key, Predicates.alwaysTrue(), Functions.<D> identity());
  }

  /**
   * Return a builder allowing you to create a new {@link DataSet} for the
   * <code>clazz</code> in this {@link FixtureFactory}.
   *
   * @param clazz
   *          the type of data the data set will contain, will be use to create
   *          a default {@link DataKey}.
   * @param <D>
   *          the type of data
   * @return a new builder
   */
  public <D> DataSetBuilder<D, D> newDataSet(Class<D> clazz) {

    DataKey<D> qualifier = DataKey.makeDefault(clazz);
    return newDataSet(qualifier);
  }

  /**
   * Add a new empty {@link DataSet} in this {@link FixtureFactory} and
   * return it.
   *
   * @param clazz
   *          the type of data the data set will contain, will be use to create
   *          a default {@link DataKey}.
   * @param <D>
   *          the type of data
   * @return a new empty data set.
   */
  public <D> DataSet<D> newEmptyDataSet(Class<D> clazz) {

    DataKey<D> qualifier = DataKey.makeDefault(clazz);
    return newEmptyDataSet(qualifier);
  }

  /**
   * Add a new empty {@link DataSet} in this {@link FixtureFactory} and
   * return it.
   *
   * @param key
   *          the data set key.
   * @param <D>
   *          the type of data
   * @return a new empty data set.
   */
  public <D> DataSet<D> newEmptyDataSet(DataKey<D> key) {

    DataSet<D> d = EmptyDataSet.ofType(key.getType());
    this.put(key, d);
    return d;
  }

  /**
   * Return a builder allowing you to create a new {@link Generator} for the
   * <code>key</code> in this {@link FixtureFactory}.
   *
   * @param key
   *          the key of the {@link DataSet} to create.
   * @param <D>
   *          the type of data
   * @return a new builder
   */
  public <D> GeneratorBuilder<D, D> newGenerator(DataKey<D> key) {
    return new GeneratorBuilder<D, D>(key, Predicates.alwaysTrue(), Functions.<D> identity());
  }

  /**
   * Return a builder allowing you to create a new {@link Generator} for this
   * data space.
   *
   * @param clazz
   *          the type of data the dataset will contain, will be use to create a
   *          default {@link DataKey}.
   * @param <D>
   *          the type of data
   * @return a new builder
   */
  public <D> GeneratorBuilder<D, D> newGenerator(Class<D> clazz) {

    DataKey<D> qualifier = DataKey.makeDefault(clazz);
    return newGenerator(qualifier);
  }

  /**
   * Add new {@link Processor} to this {@link FixtureFactory}.
   *
   * @param processor the processor to add.
   */
  @SuppressWarnings("unchecked")
  public void newProcessor(Processor<? super S> processor) {
    this.processors.add((Processor<S>) processor);
  }

//-------------------------------------------------------------------------------------------------------------------------------------------------
 //
 // Accessor methods
 //
 // -------------------------------------------------------------------------------------------------------------------------------------------------


  /*
   * (non-Javadoc)
   *
   * @see com.expedia.gps.fixtures.DataSets#of(java.lang.Class)
   */
  @Override
  public <D> DataSet<D> dataset(Class<D> clazz) {
    Preconditions.checkNotNull(clazz);
    DataKey<D> ref = getKeyFor(clazz);
    return dataset(ref);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.expedia.gps.fixtures.DataSets#of(com.expedia.gps.fixtures.Qualifier)
   */
  @Override
  public <D> DataSet<D> dataset(DataKey<D> key) {

    LOG.trace("lookup [{}] in [{}] domain", key, FixtureFactory.this.getName());

    return doGet(key);
  }

  public FixtureFactory<S> setSizeOf(Class<?> key, Integer numberOfElements) {
    return setSizeOf(getKeyFor(key), numberOfElements);
  }


  public FixtureFactory<S> setSizeOf(DataKey<?> key, Integer numberOfElements) {

    Preconditions.checkState(datasetKeys().contains(key), "Cannot set the number of elements of %s : No dataset exists for this key.", key);
    this.numberOfItemsMap.put(key, numberOfElements);

    return this;
  }

  public FixtureFactory<S> setEmpty(Class<?>...clazzes) {
    for(Class<?> k:clazzes){
      setSizeOf(k, 0);
    }

    return this;
  }


  public FixtureFactory<S> setEmpty(DataKey<?>...keys) {

    for(DataKey<?> k:keys){
      setSizeOf(k, 0);
    }

    return this;

  }

  @Override
  public Integer sizeOf(Class<?> key) {
    return sizeOf(getKeyFor(key));
  }
  @Override
  public Integer sizeOf(DataKey<?> key) {

    Integer n = null;

    Fixture<S> parent = this.getParent();

    n = numberOfItemsMap.get(key);

    if (n == null && parent != null) {
      n = parent.sizeOf(key);
    }

    if (n == null) {
      DataSet<?> d = this.dataSetMap.get(key);
      if (d != null) {
        n = d.isGenerated() ? getSpecification().getDefaultNumberOfItems() : d.list()
            .size();
      }
    }


    return n;
  }


  <D> DataSet<D> doGet(DataKey<D> key) {
    Preconditions
        .checkNotNull(key);

      DataSet<D> ds = doGetFromThisDomain(key);

      if (ds == null && parent != null) {
        ds = delegateGetToParent(key);
      }

      if (ds == null) {
        throw new DataSetNotFoundException("No dataset found for key " + key);
      } else {
        LOG.trace("FOUND [{}] as a [{}] in [{}] domain", new Object[]{key, ds.toString(), FixtureFactory.this.getName()});
        return ds;
      }

  }




  private <D> DataSet<D> doGetFromThisDomain(DataKey<D> key) {
    @SuppressWarnings("unchecked")
    DataSet<D> ds = (DataSet<D>) dataSetMap.get(key);
    return ds;
  }

  private <D> DataSet<D> delegateGetToParent(DataKey<D> key) {
    DataSet<D> ds;
    LOG.trace("delegating to parent");
    ds = parent.dataset(key);
    if (ds != null && ds.isGenerated()) {
      ds = regenerateData(key, ds);
    }
    return ds;
  }

  private <D> DataSet<D> regenerateData(final DataKey<D> key, DataSet<D> ds) {
    LOG.trace("found the generated dataset in the parent domain, regenerating it for {} domain", FixtureFactory.this.getName());
    GenerationStrategy<D, ? super S> s = strategy(key);
    if (s != null) {
      DataSet<D> gd = null;
      if(ds.isConstant()) {
        gd =  new GeneratedDataSet<D,S>(FixtureFactory.this, s, key, eventBus);
      }else{
        gd = new GeneratorImpl<D, S>(FixtureFactory.this, s, key.getType());
      }

      if (ds.isPersistent()) {
        ds = new PersistentDataSet<D>(gd, new Supplier<DataStore<D>>(){

          @SuppressWarnings("unchecked")
          @Override
          public DataStore<D> get() {
            if(FixtureFactory.this.isPersistent()){
              return FixtureFactory.this.getDataStoreProvider().get(key);
            }else{
              return (DataStore<D>) DataStore.IDENTITY;
            }

          }

        }, this.getRandomizer());
      } else {
        ds = gd;
      }

      put(key, ds);
    }
    return ds;
  }

  @Override
  public Iterable<DataSet<?>> datasets() {
    return parent == null ? dataSetMap.values() : FluentIterable.from(Iterables.concat(dataSetMap.values(), parent.datasets()));
  }

  @Override
  public Set<DataKey<?>> datasetKeys() {

    return parent == null ? dataSetMap.keySet() : FluentIterable.from(Iterables.concat(dataSetMap.keySet(), parent.datasetKeys())).toSet();

  }

  @Override
  public <D> Generator<D> generator(Class<D> clazz) {
    GenerationStrategy<D, ? super S> gen = strategy(clazz);
    if (gen == null) {
      throw new GeneratorNotFoundException("No generator found for key " + clazz);
    } else {
      return new GeneratorImpl<D, S>(FixtureFactory.this, gen, clazz);
    }
  }

  @Override
  public <D> Generator<D> generator(DataKey<D> key) {
    GenerationStrategy<D, ? super S> gen = strategy(key);

    if (gen == null) {
      throw new GeneratorNotFoundException("No generator found for key " + key);
    } else {
      return new GeneratorImpl<D, S>(FixtureFactory.this, gen, key.getType());
    }

  }

  @Override
  public Iterable<GenerationStrategy<?, ? extends DataSpecification>> strategies() {
    return parent == null ? generatorMap.values() : FluentIterable.from(Iterables.concat(generatorMap.values(), parent.strategies()));
  }

  @Override
  public Set<DataKey<?>> strategyKeys() {
    return parent == null ? generatorMap.keySet() : FluentIterable.from(Iterables.concat(generatorMap.keySet(), parent.strategyKeys())).toSet();
  }

  @Override
  public <D> GenerationStrategy<D, S> strategy(Class<D> clazz) {
    DataKey<D> key = getKeyFor(clazz);
    return strategy(key);
  }

  @Override
  public <D> GenerationStrategy<D, S> strategy(DataKey<D> key) {
    Preconditions.checkNotNull(key);
    @SuppressWarnings("unchecked")
    GenerationStrategy<D, S> gen = (GenerationStrategy<D, S>) generatorMap.get(key);

    if (gen == null && getParent() != null) {
      return getParent().strategy(key);
    }
    return gen;
  }

  <D> DataKey<D> getKeyFor(Class<D> clazz) {
    Preconditions.checkNotNull(clazz);
    DataKey<D> key = DataKey.makeDefault(clazz);
    return key;
  }

  /**
   * Register a generation strategy under the specified <code>key</code>.
   *
   * TODO make one method instead of two that takes a data set and a generator
   *
   * @param key
   *          the key
   * @param dataset
   *          the data set
   * @param <D>
   *          the type of data generated
   */
  <D> void put(DataKey<D> key, DataSet<D> dataset) {
    dataSetMap.put(key, dataset);
    this.version ++;
    eventBus.post(new DataSetRegistered(key, this, dataset.isGenerated(), dataset.isPersistent()));
  }

  /**
   * Register a generation strategy under the specified qualifier.
   *
   * TODO make one method instead of two that takes a data set and a generator
   *
   * @param key
   *          the key
   * @param strategy
   *          the generation strategy
   * @param <D>
   *          the type of data generated
   */
   <D> void put(DataKey<D> key, GenerationStrategy<D, ? super S> strategy) {
    generatorMap.put(key, strategy);

  }

  /**
   * Remove the {@link DataSet} identified by <code>key</code> from this {@link FixtureFactory}.
   *
   * @param key
   *          the qualifier associated to the {@link DataSet}.
   * @param <D>
   *          the type of data
   * @return this instance
   */
  @SuppressWarnings("unchecked")
  public <D> DataSet<D> remove(DataKey<D> key) {
    generatorMap.remove(key);
    DataSet<D> removed = (DataSet<D>) dataSetMap.remove(key);

    if(removed !=null) {
      this.version ++;
      eventBus.post(new DataSetRemoved(key, this));
    }

    return removed;
  }

  /**
   * Remove the specified {@link DataSet} of this instance.
   *
   * @param type
   *          the implicit key to the {@link DataSet}.
   * @param <D>
   *          the type of data
   * @return this instance
   */
  public <D> DataSet<D> remove(Class<D> type) {

    return remove(DataKey.makeDefault(type));
  }

  @Override
  public EventBus getEventBus() {
    return this.eventBus;
  }


  @Subscribe
  public void captureChildrenNodeEvent(Object event){
    this.eventBus.post(event);
  }

  // -------------------------------------------------------------------------------------------------------------------------------------------------
  //
  // Builder implementations
  //
  // -------------------------------------------------------------------------------------------------------------------------------------------------

  /**
   * Builder of {@link DataSet}.
   *
   * @author ngagnon
   *
   * @param <T>
   *          the type of data that will be managed by the new {@link DataSet}.
   */
  public class DataSetBuilder<D, T> {
    private final DataKey<D> originalKey;
    private Predicate<? super T> filter;
    private final Function<? super T, D> converter;
    private boolean persistent;

    DataSetBuilder(DataKey<D> originalQualifier, Predicate<? super T> filter, Function<? super T, D> converter) {
      this.filter = filter;
      this.converter = converter;
      this.originalKey = originalQualifier;
    }

    /**
     * Make this {@link DataSet} persistent, generated data will be persisted.
     *
     * @return this builder
     */
    public final DataSetBuilder<D, T> persistent() {
      this.persistent = true;
      return this;
    }

    /**
     * Make this {@link DataSet} persistent if <code>persistent</code> is true, generated data will be persisted.
     *
     * @param persistent true if the dataset to build should be persistent.
     * @return this builder
     */
    public final DataSetBuilder<D, T> persistent(boolean persistent) {
      this.persistent = persistent;
      return this;
    }

    /**
     * Filter the content of the {@link DataSet} to expose only what matches the <code>aFilter</code>.
     *
     * @param aFilter the filter to use.
     * @return this builder
     */
    public final DataSetBuilder<D, T> filtered(Predicate<? super T> aFilter) {
      injector.inject(aFilter);
      this.filter = Predicates.and(this.filter, aFilter);
      return this;
    }

    /**
     * Convert the content of the {@link DataSet} to a new type using <code>aConverter</code>.
     *
     * @param aConverter the converter to use.
     * @param <NEW_TYPE> the output type of the converter
     * @return this builder
     */
    public final <NEW_TYPE> DataSetBuilder<D, NEW_TYPE> transformed(Function<? super NEW_TYPE, ? extends T> aConverter) {
      injector.inject(aConverter);
      Function<? super NEW_TYPE, D> newConverter = Functions.compose(this.converter, aConverter);
      Predicate<? super NEW_TYPE> newPredicate = Predicates.compose(this.filter, aConverter);
      return new DataSetBuilder<D, NEW_TYPE>(originalKey, newPredicate, newConverter);
    }

    /**
     * Build a {@link DataSet} composed of the provided <code>elements</code>.
     *
     * @param elements the elements that will compose the new {@link DataSet}
     * @return a new static data set
     */
    @SafeVarargs
    public final DataSet<D> composedOf(T... elements) {
      Iterable<T> items = Iterables.filter(Arrays.asList(elements), filter);
      DataSet<D> dataset = new GenericDataSet<D>(Iterables.transform(items, converter), originalKey.getType(), getRandomizer());
      addToDataDomain(dataset);
      return dataset;
    }

    /**
     * Build a {@link DataSet} composed of the provided <code>elements</code>.
     *
     * @param elements the elements that will compose the new {@link DataSet}
     * @return a new static data set
     */
    public final DataSet<D> composedOf(Iterable<? extends T> elements) {
      Iterable<? extends T> items = Iterables.filter(elements, filter);
      DataSet<D> dataset = new GenericDataSet<D>(Iterables.transform(items, converter), originalKey.getType(), getRandomizer());
      addToDataDomain(dataset);
      return dataset;
    }


    /**
     * Build a {@link DataSet} composed of the provided <code>datasets</code>.  The passed in <code>datasets</code> are lazily loaded when
     * the returned {@link DataSet} is first accessed.
     *
     * @param datasets the data sets that will compose this new {@link DataSet}
     * @return a new static data set
     */
    @SafeVarargs
    public final DataSet<D> composedOf(DataSet<? extends T>... datasets) {

      List<DataSet<? extends T>> datasetList = Arrays.asList(datasets);

      Function<DataSet<? extends T>, Iterable<? extends T>> toIterable = new Function<DataSet<? extends T>, Iterable<? extends T>>() {
        @Override
        public Iterable<? extends T> apply(DataSet<? extends T> ds) {
          return ds.get();
        }
      };

      Iterable<D> items = FluentIterable.from(datasetList).transformAndConcat(toIterable).filter(filter).transform(converter);
      DataSet<D> dataset = new GenericDataSet<D>(Suppliers.ofInstance(items), originalKey.getType(), getRandomizer());
      addToDataDomain(dataset);
      return dataset;
    }

    /**
     * Build a {@link DataSet} generated by a <code>generator</code> which
     * implements the Goggle guava's {@link Supplier} interface.
     *
     * @param generator
     *          the generator to use
     * @param numberOfItems
     *          the number of items to generate
     * @return a new generated data set
     */
    public final DataSet<D> generatedBy(final Supplier<? extends T> generator, final int numberOfItems) {

      DataSet<D> dataset = currentFixtureSupplier.execute(new Callable<DataSet<D>>() {
        @Override
        public DataSet<D> call() {

          return generatedBy(
              new SupplierGenerationStrategyAdapter<>(originalKey, injector.inject(generator),
                  GeneratorAnnotationHelper.getAffectedDataSet(generator.getClass())), numberOfItems);

        }
      }, FixtureFactory.this);
      return dataset;
    }

    /**
     * Build a {@link DataSet} generated by a <code>generator</code> which
     * implements the Goggle guava's {@link Supplier} interface.
     *
     * @param generator
     *          the generator to use
     * @return a new generated data set
     */
    public final DataSet<D> generatedBy(final Supplier<? extends T> generator) {

      DataSet<D> dataset = currentFixtureSupplier.execute(new Callable<DataSet<D>>() {
        @Override
        public DataSet<D> call() {
          return generatedBy(new SupplierGenerationStrategyAdapter<>(originalKey, injector.inject(generator),
              GeneratorAnnotationHelper.getAffectedDataSet(generator.getClass())));

        }
      }, FixtureFactory.this);
      return dataset;
    }

    /**
     * Build a {@link DataSet} generated by an implicit <code>strategy</code>
     * which generate a predetermined number of items.
     *
     * @param strategy
     *          the strategy to use
     * @return a new generated data set
     */
    public final DataSet<D> generatedAsIterableBy(final Supplier<? extends Iterable<T>> strategy) {
      DataSet<D> dataset = currentFixtureSupplier.execute(new Callable<DataSet<D>>() {
        @Override
        public DataSet<D> call() {

          return generatedBy(new IterableSupplierGenerationStrategyAdapter<>(injector.inject(strategy),
              GeneratorAnnotationHelper.getAffectedDataSet(strategy.getClass())));

        }
      }, FixtureFactory.this);
      return dataset;
    }



    /**
     * Build a {@link DataSet} generated by a generic <code>strategy</code>.
     *
     * @param strategy
     *          the strategy to use
     * @return a new generated data set
     */
    public final DataSet<D> generatedBy(final GenerationStrategy<? extends T, ? super S> strategy, final Integer numberOfItems) {

      DataSet<D> dataset = generatedBy(strategy);

      if (numberOfItems != null) {
        FixtureFactory.this.setSizeOf(originalKey, numberOfItems);
      }

      return dataset;
    }

    /**
     * Build a {@link DataSet} generated by a generic <code>strategy</code>.
     *
     * @param strategy
     *          the strategy to use
     * @return a new generated data set
     */
    public final DataSet<D> generatedBy(final GenerationStrategy<? extends T, ? super S> strategy) {

      GenerationStrategy<D, S> derivedStrategy = new ContextualGenerationStrategyDecorator<>(new TransformedStrategy<D, T, S>(strategy, filter,
          converter), originalKey, currentFixtureSupplier);

      GeneratedDataSet<D, S> dataset = new GeneratedDataSet<D, S>(FixtureFactory.this, derivedStrategy, originalKey, eventBus);
      FixtureFactory.this.put(originalKey, derivedStrategy);

      addToDataDomain(dataset);

      return dataset;

    }

    // TODO : add a generatedBy from a list of generation strategy key

    /**
     * Build a {@link DataSet} materialized from existing {@link DataSet} of this {@link FixtureFactory}.  The resulting {@link DataSet} is
     * a view of the {@link DataSet} is materialized from.
     *
     * @param keys the default keys of the data sets
     * @return a new generated data set
     */
    @SafeVarargs
    public final DataSet<D> materalizedFrom(final Class<? extends T>... keys) {
      return materalizedFrom(Iterables.transform(Arrays.asList(keys), new Function<Class<? extends T>, DataKey<? extends T>>() {
        @Override
        public DataKey<? extends T> apply(Class<? extends T> input) {
          return DataKey.makeDefault(input);
        }
      }));
    }

    /**
     * Build a {@link DataSet} materialized from existing {@link DataSet} of this {@link FixtureFactory}.  The resulting {@link DataSet} is
     * a view of the {@link DataSet} is materialized from.
     *
     * @param keys the keys of the data sets
     * @return a new generated data set
     */
    @SafeVarargs
    public final DataSet<D> materalizedFrom(final DataKey<? extends T>... keys) {
      return materalizedFrom(Arrays.asList(keys));
    }

    /**
     * Build a {@link DataSet} materialized from existing {@link DataSet} of this {@link FixtureFactory}.  The resulting {@link DataSet} is
     * a view of the {@link DataSet} is materialized from.
     *
     * @param keys the keys of the data sets
     * @return a new generated data set
     */
    public final DataSet<D> materalizedFrom(final Iterable<DataKey<? extends T>> keys) {
      GenerationStrategy<D, S> derivedStrategy = new TransformedStrategy<D, T, S>(new DataSetAggregationStrategy<T, S>(keys), filter, converter);
      GeneratedDataSet<D,S> dataset = new GeneratedDataSet<D,S>(FixtureFactory.this, derivedStrategy, originalKey, eventBus);
      FixtureFactory.this.put(originalKey, derivedStrategy);

      addToDataDomain(dataset);
      return dataset;
    }

    private void addToDataDomain(DataSet<D> dataset) {
      if (persistent) {
        dataset = new PersistentDataSet<>(dataset, new Supplier<DataStore<D>>(){

          @SuppressWarnings("unchecked")
          @Override
          public DataStore<D> get() {
            //If this fixture factory is not persistent, we return a transient datastore but we
            //need to keep the persistent nature of the dataset since another fixture factory inheriting from
            //this factory may be persistent
            if(FixtureFactory.this.isPersistent()){
              return FixtureFactory.this.getDataStoreProvider().get(originalKey);
            }else{
              return (DataStore<D>) DataStore.IDENTITY;
            }
          }

        }, randomizer);
      }
      FixtureFactory.this.put(originalKey, dataset);
    }

  }

  /**
   * Builder of {@link DataSet}.
   *
   * @author ngagnon
   *
   * @param <T>
   *          the type of data that will be managed by the new {@link DataSet}.
   */
  public class GeneratorBuilder<D, T> {
    private final DataKey<D> originalKey;
    private Predicate<? super T> filter;
    private final Function<? super T, D> converter;

    GeneratorBuilder(DataKey<D> origoriginalKey, Predicate<? super T> filter, Function<? super T, D> converter) {
      this.filter = filter;
      this.converter = converter;
      this.originalKey = origoriginalKey;
    }

    /**
     * Filter the content of the {@link Generator} being built to expose only what matches the <code>filter</code>.
     *
     * @param filter the filter to use.
     * @return this builder
     */
    public GeneratorBuilder<D, T> filtered(Predicate<? super T> filter) {
      injector.inject(filter);
      this.filter = Predicates.and(this.filter, filter);
      return this;
    }

    /**
     * Convert the content of the {@link Generator} to a new type using the <code>converter</code>.
     *
     * @param converter the converter to use.
     * @param <NEW_TYPE> the output type of the converter
     * @return this builder
     */
    public <NEW_TYPE> GeneratorBuilder<D, NEW_TYPE> transformed(Function<? super NEW_TYPE, T> converter) {

      injector.inject(converter);
      Function<? super NEW_TYPE, D> newConverter = Functions.compose(this.converter, converter);
      Predicate<? super NEW_TYPE> newPredicate = Predicates.compose(this.filter, converter);
      return new GeneratorBuilder<D, NEW_TYPE>(originalKey, newPredicate, newConverter);
    }

    /**
     * Build a {@link Generator} that delegate to a generic <code>strategy</code>.
     *
     * @param strategy the strategy to use
     * @return a new generated data set
     */
    public Generator<D> generatedBy(final GenerationStrategy<? extends T, ? super S> strategy) {

      GenerationStrategy<D, S> derivedStrategy = new ContextualGenerationStrategyDecorator<>(new TransformedStrategy<D, T, S>(strategy, filter,
          converter), originalKey, currentFixtureSupplier);

      FixtureFactory.this.put(originalKey, derivedStrategy);

      Generator<D> generator = new GeneratorImpl<D, S>(FixtureFactory.this, derivedStrategy, originalKey.getType());

      addToDataDomain(generator);

      return generator;
    }

    /**
     * Build a {@link Generator} that delegate to a generic <code>strategy</code>.
     *
     * @param strategy the strategy to use
     * @return a new generated data set
     */
    public Generator<D> generatedBy(final GenerationStrategy<? extends T, ? super S> strategy, final Integer numberOfItems) {

      Generator<D> generator = generatedBy(strategy);

      if (numberOfItems != null) {
        FixtureFactory.this.setSizeOf(originalKey, numberOfItems);
      }

      return generator;

    }


    /**
     * Build a {@link Generator} that delegate to a simple <code>strategy</code>.
     *
     * @param generator the strategy to use
     * @param numberOfItems override the default number of items of the {@link DataSpecification}.
     * @return a new generated data set
     */
    public Generator<D> generatedBy(final Supplier<? extends T> generator) {

      return  currentFixtureSupplier.execute(new Callable<Generator<D>>(){
        @Override
        public  Generator<D> call(){
          return generatedBy(new SupplierGenerationStrategyAdapter<>(originalKey, injector.inject(generator),
              GeneratorAnnotationHelper.getAffectedDataSet(generator.getClass())));
        }
      }, FixtureFactory.this);

    }


    /**
     * Build a {@link Generator} that delegate to a simple <code>strategy</code>.
     *
     * @param generator the strategy to use
     * @param numberOfItems override the default number of items of the {@link DataSpecification}.
     * @return a new generated data set
     */
    public Generator<D> generatedBy(final Supplier<? extends T> generator, final int numberOfItems) {


     return  currentFixtureSupplier.execute(new Callable<Generator<D>>(){
        @Override
        public  Generator<D> call(){
          return generatedBy(new SupplierGenerationStrategyAdapter<>(originalKey, injector.inject(generator),
              GeneratorAnnotationHelper.getAffectedDataSet(generator.getClass())), numberOfItems);
        }
      }, FixtureFactory.this);


    }


    private void addToDataDomain(Generator<D> dataset) {
      FixtureFactory.this.put(originalKey, dataset);
    }
  }



}
