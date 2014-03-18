package org.magenta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import org.magenta.core.DataDomainAggregator;
import org.magenta.core.EmptyDataSet;
import org.magenta.core.GeneratedDataSet;
import org.magenta.core.GeneratorImpl;
import org.magenta.core.GenericDataSet;
import org.magenta.core.PersistentDataSet;
import org.magenta.core.RestrictionHelper;
import org.magenta.generators.DataSetAggregationStrategy;
import org.magenta.generators.GeneratorAnnotationHelper;
import org.magenta.generators.ImplicitGenerationStrategyAdapter;
import org.magenta.generators.NonReentrantDecorator;
import org.magenta.generators.SimpleGenerationStrategyAdapter;
import org.magenta.generators.SupplierBasedSimpleGenerationStrategyAdapter;
import org.magenta.generators.TransformedStrategy;
import org.magenta.random.Randoms;
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

/**
 * This class allow the management of {@link DataSet}. Use this class to add,
 * get and remove {@link DataSet}s.
 *
 * @author normand
 *
 */
public class DataDomainManager<S extends DataSpecification> implements DataDomain<S> {

  private static final  Logger LOG = LoggerFactory.getLogger(DataDomainManager.class);

  private final DataDomain<S> parent;

  private final DataStoreProvider dataStoreProvider;

  private final Randoms randomizer;

  private final S specification;

  private final Map<DataKey<?>, DataSet<?>> dataSetMap;
  private final Map<DataKey<?>, GenerationStrategy<?, ? extends DataSpecification>> generatorMap;
  private final List<Processor<S>> processors;


  private final ThreadLocal<Stack<DataKey<?>>> generationCallStack;

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
  private DataDomainManager(String name, DataDomain<S> parent, S specification, Randoms randomizer, DataStoreProvider datastoreProvider,
      ThreadLocal<Stack<DataKey<?>>> getCallstack) {
    this.name = name;
    this.parent = parent;
    this.specification = specification;
    this.randomizer = randomizer;
    this.dataStoreProvider = datastoreProvider;

    this.generationCallStack = getCallstack;

    this.dataSetMap = new HashMap<DataKey<?>, DataSet<?>>();
    this.generatorMap = new HashMap<DataKey<?>, GenerationStrategy<?, ?>>();
    this.processors = Lists.newArrayList();



  }

  // -------------------------------------------------------------------------------------------------------------------------------------------------
  //
  // Factory Methods
  //
  // -------------------------------------------------------------------------------------------------------------------------------------------------

  /**
   * Create a new root {@link DataDomainManager}.
   *
   * @param name
   *          the name of this data domain
   * @param specification
   *          the {@link DataSpecification} of this data domain
   * @param randomizer
   *          the randomizer to use
   * @param <S>
   *          the type of {@link DataSpecification}
   * @return a new {@link DataDomainManager}
   */
  public static <S extends DataSpecification> DataDomainManager<S> newRoot(String name, S specification, Randoms randomizer) {
    return new DataDomainManager<S>(name, null, specification, randomizer, null, new ThreadLocal<Stack<DataKey<?>>>());
  }

  /**
   * Open a new scope using this {@link DataDomainManager} as parent. The new
   * scope is itself a {@link DataDomainManager} where new {@link DataSet} may
   * be added that won't be visible for this {@link DataDomainManager}.
   *
   * @param name
   *          the name of the new node.
   * @return a new {@link DataDomainManager} child of this one.
   */
  public DataDomainManager<S> newNode(String name) {
    return new DataDomainManager<S>(name, this, this.getSpecification(), this.randomizer, this.dataStoreProvider,this.generationCallStack);
  }

  /**
   * Open a new scope using this {@link DataDomainManager} as parent. The new
   * scope is itself a {@link DataDomainManager} where new {@link DataSet} may
   * be added that won't be visible for this {@link DataDomainManager}.
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
  public <X extends S> DataDomainManager<X> newNode(String name, X dataspecification) {
    // Cast is safe here, because the parent will be in fact used with its child
    // DataSpecification which extends the parent data specification
    return new DataDomainManager<X>(name, (DataDomainManager<X>) this, dataspecification, this.randomizer, this.dataStoreProvider,
       this.generationCallStack);
  }

  /**
   * Open a new scope delegating to <code>dataDomain</code> and using this
   * {@link DataDomainManager} as parent. The new scope is itself a
   * {@link DataDomainManager} where new {@link DataSet} may be added that won't
   * be visible for this {@link DataDomainManager}.
   *
   * @param dataDomain
   *          the delegated data domain
   * @return a new DataSetManager child of this one.
   */
  public DataDomainManager<S> newNode(DataDomain<? super S> dataDomain) {
    DataDomainAggregator<S> aggregation = new DataDomainAggregator<S>(dataDomain, this);
    return new DataDomainManager<S>("child of " + this.getName(), aggregation, this.getSpecification(), randomizer, this.dataStoreProvider,
         this.generationCallStack);
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
  public DataDomain<S> getParent() {
    return parent;
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
  public Randoms getRandomizer() {
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
   * DataDomainManager tourismDomain = TourismDomain.createDomain();
   * City paris = CityBuilder.build(&quot;Paris&quot;);
   * Monument monument = tourismDomain.restrictTo(paris).dataset(Monument.class).any();
   *
   * Assert.assertEquals(paris, monument.getCity());
   *
   * </pre>
   *
   * @param objects
   *          an array of object from which the dataset will be created
   * @return this FixturesManager
   */
  public DataDomainManager<S> restrictTo(Object... objects) {

    DataDomainManager<S> child = newNode("restricted " + this.getName());

    RestrictionHelper.applyRestrictions(child, objects);

    return child;
  }

  /**
   * Return a new {@link DataDomainManager} node which return fixed values for
   * every {@link DataSet} identified by <code>classes</code>. Normally,
   * generated data set are regenerated for new node but this method "fixes" the
   * generated values as they are currently found in this
   * {@link DataDomainManager}.
   *
   * @param classes
   *          the dataset default identifiers.
   * @return a new {@link DataDomainManager} node.
   */
  public DataDomainManager<S> fix(Class<?>... classes) {

    DataKey<?>[] keys = FluentIterable.from(Arrays.asList(classes)).transform(DataKey.classToKey()).toArray(DataKey.class);

    return fix(keys);
  }

  /**
   * Return a new {@link DataDomainManager} node which return fixed values for
   * every {@link DataSet} identified by <code>keys</code>. Normally, generated
   * data set are regenerated for new node but this method "fixes" the generated
   * values as they are currently found in this {@link DataDomainManager}.
   *
   * @param keys
   *          the data set identifiers.
   * @return a new {@link DataDomainManager} node.
   */
  @SuppressWarnings("unchecked")
  public DataDomainManager<S> fix(DataKey<?>... keys) {
    DataDomainManager<S> child = newNode("frozen " + this.getName());
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
   * <code>key</code> in this {@link DataDomainManager}.
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
   * <code>clazz</code> in this {@link DataDomainManager}.
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
   * Add a new empty {@link DataSet} in this {@link DataDomainManager} and
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
   * Add a new empty {@link DataSet} in this {@link DataDomainManager} and
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
   * <code>key</code> in this {@link DataDomainManager}.
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
   * Add new {@link Processor} to this {@link DataDomainManager}.
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

    LOG.trace("lookup [{}] in [{}] domain", key, DataDomainManager.this.getName());

    return doGet(key);
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
        LOG.trace("FOUND [{}] in [{}] domain", key, DataDomainManager.this.getName());
        return ds;
      }

  }

  /*private <D> void processRelations(DataKey<D> key) {
    // prevent infinite loop
    if (!isCurrentlyProcessingRelationOfThisKey(key)) {
      LOG.trace("lookup relations of [{}]", key);
      try {
        pushOnCallStack(key);
        Iterable<DataKey<?>> relations = computeRelationsOf(key);

        for (DataKey<?> relationKey : relations) {
          LOG.trace("{} is related to {} and must be generated first", relationKey, key);
          // call "get" on the returned dataset so it triggers data generation
          // which is what we want since this dataset is a relation to the one
          // we are looking for
          // and must be generated before that one
          if(!isCurrentlyGeneratingThisDataSet(relationKey)){
            doGet(relationKey).get();
          }
        }

        executeProcessorsFor(key);

      } finally {
        popFromCallStack();

      }
    }
  }*/

  /*private <D> boolean isCurrentlyProcessingRelationOfThisKey(DataKey<D> key) {
    return currentCallStack.get() != null && currentCallStack.get().contains(key);
  }

  private void pushOnCallStack(DataKey<?> key) {
    if (currentCallStack.get() == null) {
      currentCallStack.set(new Stack<DataKey<?>>());
    }
    currentCallStack.get().push(key);
  }

  private void popFromCallStack() {
    currentCallStack.get().pop();
  }

  private <D> boolean isCurrentlyGeneratingThisDataSet(DataKey<D> key) {
    return generationCallStack.get() != null && generationCallStack.get().contains(key);
  }*/

  public void pushOnGenerationCallStack(DataKey<?> key) {
    if (generationCallStack.get() == null) {
      generationCallStack.set(new Stack<DataKey<?>>());
    }

    if(generationCallStack.get().contains(key)){
      throw new CycleDetectedInGenerationException("Infinite loop detected for generation of key " + displayGenerationCallStack(key));
    }

    generationCallStack.get().push(key);
  }

  public void popFromGenerationCallStack() {
    if(!generationCallStack.get().isEmpty()) {
      generationCallStack.get().pop();
    }else{
      LOG.warn("Attempt to pop from the get call stack but it is empty");
    }
  }

  private String displayGenerationCallStack(DataKey<?> current) {
    StringBuilder sb = new StringBuilder("Stack:\n");
    sb.append(current).append('\n');
    for(DataKey<?> key:generationCallStack.get()){
      sb.append(key).append('\n');
    }
    return sb.toString();
  }




  /*private <D> Collection<DataKey<?>> computeRelationsOf(DataKey<D> key) {



      GenerationStrategy<?, ?> gs = strategy(key);
      if (gs != null) {
        return Lists.newArrayList(gs.getTriggeredGeneratedDataKeys());
      }

    return Collections.emptyList();



  }*/

  /*private void executeProcessorsFor(final DataKey<?> key) {
    Iterable<Processor<S>> matchingProcessor = Iterables.filter(processors, new Predicate<Processor<S>>() {

      @Override
      public boolean apply(Processor<S> input) {
        if (input.getAffectedDataSetKeys().contains(key)) {
          return true;
        }
        return false;
      }

    });

    for (Processor<S> p : matchingProcessor) {
      LOG.trace("processing {} with {}", key, p);
      p.process(DataDomainManager.this);
    }
  }*/

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

  private <D> DataSet<D> regenerateData(DataKey<D> key, DataSet<D> ds) {
    LOG.trace("found the generated dataset in the parent domain, regenerating it for {} domain", DataDomainManager.this.getName());
    GenerationStrategy<D, ? super S> s = strategy(key);
    if (s != null) {
      ds = new GeneratedDataSet<D>(DataDomainManager.this, s, key.getType());
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
      return new GeneratorImpl<D, S>(DataDomainManager.this, gen, clazz);
    }
  }

  @Override
  public <D> Generator<D> generator(DataKey<D> key) {
    GenerationStrategy<D, ? super S> gen = strategy(key);

    if (gen == null) {
      throw new GeneratorNotFoundException("No generator found for key " + key);
    } else {
      return new GeneratorImpl<D, S>(DataDomainManager.this, gen, key.getType());
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
   * Remove the {@link DataSet} identified by <code>key</code> from this {@link DataDomainManager}.
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
    return (DataSet<D>) dataSetMap.remove(key);
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
     * Build a {@link DataSet} generated by a <code>generator</code> which implements the Goggle guava's {@link Supplier} interface.
     *
     * @param generator the generator to use
     * @param numberOfItems the number of items to generate
     * @return a new generated data set
     */
    public final DataSet<D> generatedBy(Supplier<? extends T> generator, int numberOfItems) {
      return generatedBy(new SupplierBasedSimpleGenerationStrategyAdapter<>(generator, numberOfItems,
          GeneratorAnnotationHelper.getAffectedDataSet(generator.getClass())));
    }

    /**
     * Build a {@link DataSet} generated by a <code>generator</code> which implements the Goggle guava's {@link Supplier} interface.
     *
     * @param generator the generator to use
     * @return a new generated data set
     */
    public final DataSet<D> generatedBy(Supplier<? extends T> generator) {
      return generatedBy(new SupplierBasedSimpleGenerationStrategyAdapter<>(generator, GeneratorAnnotationHelper.getAffectedDataSet(generator
          .getClass())));
    }

    /**
     * Build a {@link DataSet} generated by an implicit <code>strategy</code> which generate a predetermined number of items.
     *
     * @param strategy the strategy to use
     * @return a new generated data set
     */
    public final DataSet<D> generatedBy(ImplicitGenerationStrategy<? extends T, ? super S> strategy) {
      return generatedBy(new ImplicitGenerationStrategyAdapter<>(strategy, GeneratorAnnotationHelper.getAffectedDataSet(strategy.getClass())));
    }

    /**
     * Build a {@link DataSet} generated by a simple <code>strategy</code>.
     * @param strategy the strategy to use
     * @return new generated data set
     */
    public final DataSet<D> generatedBy(SimpleGenerationStrategy<? extends T, ? super S> strategy) {
      return generatedBy(new SimpleGenerationStrategyAdapter<>(strategy, GeneratorAnnotationHelper.getAffectedDataSet(strategy.getClass())));
    }

    /**
     * Build a {@link DataSet} generated by a simple <code>strategy</code>.
     * @param strategy the strategy to use
     * @param numberOfItems override the strategy preferred number of items.
     * @return new generated data set
     */
    public final DataSet<D> generatedBy(SimpleGenerationStrategy<? extends T, ? super S> strategy, int numberOfItems) {
      return generatedBy(new SimpleGenerationStrategyAdapter<>(strategy, numberOfItems, GeneratorAnnotationHelper.getAffectedDataSet(strategy
          .getClass())));
    }

    /**
     * Build a {@link DataSet} generated by a generic <code>strategy</code>.
     *
     * @param strategy the strategy to use
     * @return a new generated data set
     */
    public final DataSet<D> generatedBy(GenerationStrategy<? extends T, ? super S> strategy) {
      GenerationStrategy<D, S> derivedStrategy = new NonReentrantDecorator<>(new TransformedStrategy<D, T, S>(strategy, filter, converter), originalKey);

      GeneratedDataSet<D> dataset = new GeneratedDataSet<D>(DataDomainManager.this, derivedStrategy, originalKey.getType());
      DataDomainManager.this.put(originalKey, derivedStrategy);

      addToDataDomain(dataset);
      return dataset;
    }

    // TODO : add a generatedBy from a list of generation strategy key

    /**
     * Build a {@link DataSet} materialized from existing {@link DataSet} of this {@link DataDomainManager}.  The resulting {@link DataSet} is
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
     * Build a {@link DataSet} materialized from existing {@link DataSet} of this {@link DataDomainManager}.  The resulting {@link DataSet} is
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
     * Build a {@link DataSet} materialized from existing {@link DataSet} of this {@link DataDomainManager}.  The resulting {@link DataSet} is
     * a view of the {@link DataSet} is materialized from.
     *
     * @param keys the keys of the data sets
     * @return a new generated data set
     */
    public final DataSet<D> materalizedFrom(final Iterable<DataKey<? extends T>> keys) {
      GenerationStrategy<D, S> derivedStrategy = new TransformedStrategy<D, T, S>(new DataSetAggregationStrategy<T, S>(keys), filter, converter);
      GeneratedDataSet<D> dataset = new GeneratedDataSet<D>(DataDomainManager.this, derivedStrategy, originalKey.getType());
      DataDomainManager.this.put(originalKey, derivedStrategy);

      addToDataDomain(dataset);
      return dataset;
    }

    private void addToDataDomain(DataSet<D> dataset) {
      if (persistent) {
        dataset = new PersistentDataSet<>(dataset, DataDomainManager.this.getDataStoreProvider().get(originalKey), randomizer);
      }
      DataDomainManager.this.put(originalKey, dataset);
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
    public Generator<D> generatedBy(GenerationStrategy<? extends T, ? super S> strategy) {
      GenerationStrategy<D, S> derivedStrategy = new TransformedStrategy<D, T, S>(strategy, filter, converter);
      DataDomainManager.this.put(originalKey, derivedStrategy);

      Generator<D> generator = new GeneratorImpl<D, S>(DataDomainManager.this, derivedStrategy, originalKey.getType());

      addToDataDomain(generator);

      return generator;
    }

    /**
     * Build a {@link Generator} that delegate to a simple <code>strategy</code>.
     *
     * @param strategy the strategy to use
     * @return a new generated data set
     */
    public Generator<D> generatedBy(SimpleGenerationStrategy<? extends T, ? super S> strategy) {

      return generatedBy(new SimpleGenerationStrategyAdapter<T, S>(strategy,
          GeneratorAnnotationHelper.getAffectedDataSet(strategy.getClass())));

    }

    /**
     * Build a {@link Generator} that delegate to a simple <code>strategy</code>.
     *
     * @param strategy the strategy to use
     * @param numberOfItems override the preferred number of items of the strategy
     * @return a new generated data set
     */
    public Generator<D> generatedBy(SimpleGenerationStrategy<? extends T, ? super S> strategy, int numberOfItems) {

      return generatedBy(new SimpleGenerationStrategyAdapter<T, S>(strategy, numberOfItems,
          GeneratorAnnotationHelper.getAffectedDataSet(strategy.getClass())));

    }

    /**
     * Build a {@link Generator} that delegate to a simple <code>strategy</code>.
     *
     * @param generator the strategy to use
     * @param numberOfItems override the default number of items of the {@link DataSpecification}.
     * @return a new generated data set
     */
    public Generator<D> generatedBy(Supplier<? extends T> generator, int numberOfItems) {

      return generatedBy(new SupplierBasedSimpleGenerationStrategyAdapter<>(generator, numberOfItems,
          GeneratorAnnotationHelper.getAffectedDataSet(generator.getClass())));

    }

    /**
     * Build a {@link Generator} generated by an implicit <code>strategy</code> which generate a predetermined number of items.
     *
     * @param strategy the strategy to use
     * @return a new generated data set
     */
    public final Generator<D> generatedBy(ImplicitGenerationStrategy<? extends T, ? super S> strategy) {
      return generatedBy(new ImplicitGenerationStrategyAdapter<>(strategy, GeneratorAnnotationHelper.getAffectedDataSet(strategy.getClass())));
    }

    private void addToDataDomain(Generator<D> dataset) {
      DataDomainManager.this.put(originalKey, dataset);
    }
  }

}
