package org.magenta;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import org.magenta.core.DataSetFunctionRegistry;
import org.magenta.core.DataSetImpl;
import org.magenta.core.FixtureContext;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.GenerationStrategyFactory;
import org.magenta.core.RestrictionHelper;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;
import org.magenta.core.data.supplier.GeneratorDataSupplier;
import org.magenta.core.data.supplier.LazyGeneratedCollectionDataSupplier;
import org.magenta.core.data.supplier.LazyGeneratedDataSupplier;
import org.magenta.core.data.supplier.StaticDataSupplier;
import org.magenta.core.data.supplier.TransformedDataSupplierDecorator;
import org.magenta.events.DataSetFound;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

public class FixtureFactory implements Fixture {

  private FixtureFactory parent;
  private DataSetFunctionRegistry registry;

  private GenerationStrategyFactory generationStrategyFactory;
  private DynamicGeneratorFactory dynamicGeneratorFactory;

  private FixtureContext context;

  FixtureFactory(FixtureFactory parent, GenerationStrategyFactory generationStrategyBuilder, DynamicGeneratorFactory generatorFactory,
      FixtureContext context) {
    this.parent = parent;
    this.generationStrategyFactory = generationStrategyBuilder;
    this.context = context;
    this.registry = new DataSetFunctionRegistry();
    this.dynamicGeneratorFactory = generatorFactory;
  }

  public FixtureFactory newChild() {
    FixtureFactory child = new FixtureFactory(this, generationStrategyFactory, dynamicGeneratorFactory, context);
    return child;
  }

  @Override
  public <D> DataSet<D> dataset(Class<D> type) {
    return dataset(DataKey.of(type));
  }

  @Override
  public <D> D any(Class<D> type, Object firstRestriction, Object...rest) {
    return restrictTo(firstRestriction, rest).dataset(DataKey.of(type)).any();
  }

  @Override
  public <D> D any(Class<D> type) {
    return dataset(DataKey.of(type)).any();
  }

  @Override
  public <D> D first(Class<D> type) {
    return dataset(DataKey.of(type)).first();
  }

  @Override
  public <D> D first(Class<D> type, Object firstRestriction, Object... rest) {
    return restrictTo(firstRestriction, rest).dataset(DataKey.of(type)).first();
  }

  @Override
  public <D> List<D> list(Class<D> type) {
    return dataset(DataKey.of(type)).list();
  }

  @Override
  public <D> List<D> list(Class<D> type, Object firstRestriction, Object...rest) {
    return restrictTo(firstRestriction, rest).dataset(DataKey.of(type)).list();
  }

  @Override
  public <D> List<D> list(Class<D> type, Integer size, Object firstRestriction, Object...rest) {
    return restrictTo(firstRestriction, rest).dataset(DataKey.of(type)).list(size);
  }

  @Override
  public <D> DataSet<D> dataset(DataKey<D> key) {
    return doGetDatasetFunction(key).apply(this);
  }

  @Override
  public FixtureFactory init(Class<?> type) {

    DataKey key = DataKey.of(type);

    newDataSet(key).composedOf(dataset(key).list());
    return this;
  }

  @Override
  public FixtureFactory init(DataKey key) {


    newDataSet(key).composedOf(dataset(key).list());
    return this;
  }

  @Override
  public FixtureFactory init(Class<?> type, int size) {

    DataKey key = DataKey.of(type);

    newDataSet(key).composedOf(dataset(key).list(size));

    return this;
  }

  @Override
  public FixtureFactory init(DataKey key, int size) {

    newDataSet(key).composedOf(dataset(key).list(size));

    return this;
  }

  private <D> Function<Fixture, DataSet<D>> doGetDatasetFunction(DataKey<D> key) {
    Function<Fixture, DataSet<D>> datasetFunction = registry.get(key);
    if (datasetFunction != null) {
      Magenta.eventBus().post(DataSetFound.from(key, this));
      return datasetFunction;
    } else {
      if (parent != null) {
        return parent.doGetDatasetFunction(key);
      }
      throw new DataSetNotFoundException(String.format("Dataset [%s] is not declared in this fixture", key));
    }
  }

  @Override
  public Set<DataKey<?>> keys() {
    return parent == null ? registry.keys() : Sets.union(registry.keys(), this.parent.keys());
  }

  public <D> DataSetBuilder<D,D> newDataSet(DataKey<D> key) {
    return new DataSetBuilder<D,D>(key, Functions.identity());
  }

  public <D> DataSetBuilder<D,D> newDataSet(Class<D> key) {
    return new DataSetBuilder<D,D>(DataKey.of(key), Functions.identity());
  }

  @SafeVarargs
  public final void newDataSetsOf(Object first, Object...rest) {
    RestrictionHelper.createDatasets(this, first, rest);
  }

  @SafeVarargs
  public final <D> void newDataSetOf(D first, D...rest) {
    newDataSetsOf(first, rest);
  }

  public <D> void newLazyDataSet(Class<D> key, Supplier<D> generator) {
    newDataSet(key).generatedBy(generator);
  }

  public <D> GeneratorBuilder<D> newGenerator(DataKey<D> key) {

    return new GeneratorBuilder<D>(key);
  }

  public <D> GeneratorBuilder<D> newGenerator(Class<D> key) {

    return new GeneratorBuilder<D>(DataKey.of(key));
  }

  @Override
  public FixtureFactory restrictTo(Object first, Object... theRest) {

    FixtureFactory child = newChild();

    RestrictionHelper.applyRestrictions(child, first, theRest);
    return child;
  }

  public class DataSetBuilder<D,S> {

    private DataKey<D> key;
    private Function<S,D> transform;

    public DataSetBuilder(DataKey<D> key, Function<S,D> transform) {
      this.key = key;
      this.transform = transform;
    }

    public <NEW_TYPE>  DataSetBuilder<D,NEW_TYPE> transformed(Function<NEW_TYPE,S> converter) {

      Function<NEW_TYPE,D> newFunction = Functions.compose(transform,converter);

      return new DataSetBuilder<D,NEW_TYPE>(key, newFunction);
    }


    // --- static datasets --- //
    public void composedOf(Iterable<S> data) {
      composedOf(ImmutableList.copyOf(data));
    }

    public void composedOf(S... items) {
      composedOf(ImmutableList.copyOf(items));
    }

    private void composedOf(ImmutableList<S> items) {
      DataSet<D> dataset = new DataSetImpl<D>(new TransformedDataSupplierDecorator<>(new StaticDataSupplier<S>(items,  (TypeToken<S>)this.key.getType()),transform,key.getType()), true);
      registry.register(key, Functions.constant(dataset));
    }

    // ---- materialized ---- //
    public void materializedFrom(Class<S> clazz){
      Function<Fixture,DataSet<D>> getDataset = cache(f -> f.dataset(clazz).transform(transform, key.getType()));
      registry.register(key, getDataset);
    }

    public void materializedFrom(DataKey<S> sourceKey){
      Function<Fixture,DataSet<D>> getDataset = cache(f -> f.dataset(sourceKey).transform(transform, key.getType()));
      registry.register(key, getDataset);
    }

    // --- generated datasets --- //

    public void autoMagicallyGenerated() {

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(buildDynamicGenerator((DataKey<S>)key), Optional.<Integer> absent())));

      registry.register(key, getDataset);

    }

    public void autoMagicallyGenerated(int numberOfItems) {

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(buildDynamicGenerator((DataKey<S>)key), Optional.of(numberOfItems))));

      registry.register(key, getDataset);

    }

    public void generatedBy(final Supplier<? extends S> generator) {
      generatedBy(generator, Optional.<Integer> absent());
    }

    public void generatedBy(final Supplier<? extends S> generator, final Integer numberOfItems) {
      generatedBy(generator, Optional.of(numberOfItems));

    }

    public void generatedBy(final GenerationStrategy<S> generator) {
      checkNotNull(generator, "generator argument is null");

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(generator,Optional.absent())));

      registry.register(key, getDataset);

    }

    protected void generatedBy(final Supplier<? extends S> generator, final Optional<Integer> numberOfItems) {

      checkNotNull(generator, "generator argument is null");

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(toGenerationStrategy(key, (Supplier<S>)generator), numberOfItems)));

      registry.register(key, getDataset);

    }

    public void generatedAsIterableBy(Supplier<? extends Iterable<S>> generator) {
      checkNotNull(generator, "generator argument is null");

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(toGenerationStrategy(key, generator))));

      registry.register(key, getDataset);
    }

    private Function<Fixture, DataSupplier<D>> toDataSupplier(final GenerationStrategy<S> strategy, Optional<Integer> numberOfItems) {
      return fixture ->
      new TransformedDataSupplierDecorator<S,D>(
          new LazyGeneratedDataSupplier<S>(
              (TypeToken<S>)this.key.getType(),
              () -> strategy.generate(fixture),
              () -> numberOfItems.isPresent() ? numberOfItems.get() : strategy.size(fixture)
              ), transform, key.getType()
          );

    }

    private Function<Fixture, DataSupplier<D>> toDataSupplier(final GenerationStrategy<? extends Iterable<S>> strategy) {
      return fixture -> new TransformedDataSupplierDecorator<S, D>(
          new LazyGeneratedCollectionDataSupplier<S>(() -> strategy.generate(fixture),  (TypeToken<S>)this.key.getType()), transform, key.getType());
    }
  }

  public class GeneratorBuilder<D> {

    private DataKey<D> key;

    public GeneratorBuilder(DataKey<D> key) {
      this.key = key;
    }

    public void generatedBy(final Supplier<D> generator) {
      generatedBy(generator, Optional.<Integer> absent());
    }

    public void generatedBy(Supplier<D> generator, Integer defaultSize) {

      generatedBy(generator, Optional.of(defaultSize));
    }

    public void generatedBy(final GenerationStrategy<D> generator) {
      checkNotNull(generator, "generator argument is null");

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(generator,Optional.absent())));

      registry.register(key, getDataset);

    }

    private void generatedBy(Supplier<D> generator, Optional<Integer> numberOfItems) {

      checkNotNull(generator, "generator argument is null");

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(toGenerationStrategy(key, generator), numberOfItems)));

      registry.register(key, getDataset);

    }

    private Function<Fixture, DataSupplier<D>> toDataSupplier(final GenerationStrategy<D> strategy, final Optional<Integer> numberOfItems) {
      return fixture ->

      new GeneratorDataSupplier<>(
          key.getType(),
          () -> strategy.generate(fixture),
          () -> (numberOfItems.isPresent() ? numberOfItems.get() : strategy.size(fixture))
          );

    }

  }

  private <X> Function<Fixture, DataSet<X>> cache(Function<Fixture, DataSet<X>> function) {
    return CacheBuilder.newBuilder().build(CacheLoader.from(function));
  }

  private <X> Function<Fixture, DataSet<X>> toDataSet(Function<Fixture, DataSupplier<X>> dataSupplier) {
    return fixture -> {
      return new DataSetImpl<>(dataSupplier.apply(fixture), true);
    };
  }

  private <X> GenerationStrategy<X> toGenerationStrategy(DataKey key,Supplier<X> generator) {
    return generationStrategyFactory.create(key, generator);
  }

  private <X> GenerationStrategy<X> buildDynamicGenerator(DataKey<X> type) {
    return dynamicGeneratorFactory.buildGeneratorOf(type, FixtureFactory.this, dynamicGeneratorFactory).get();
  }





}