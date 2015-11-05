package org.magenta;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.magenta.core.DataSetFunctionRegistry;
import org.magenta.core.DataSetImpl;
import org.magenta.core.FixtureContext;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.SimpleGenerationStrategy;
import org.magenta.core.GenerationStrategyFactory;
import org.magenta.core.RestrictionHelper;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;
import org.magenta.core.data.supplier.GeneratorDataSupplier;
import org.magenta.core.data.supplier.LazyGeneratedCollectionDataSupplier;
import org.magenta.core.data.supplier.LazyGeneratedDataSupplier;
import org.magenta.core.data.supplier.StaticDataSupplier;

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
    return new FixtureFactory(this, generationStrategyFactory, dynamicGeneratorFactory, context);
  }

  @Override
  public <D> DataSet<D> dataset(Class<D> type) {
    return dataset(DataKey.of(type));
  }

  @Override
  public <D> DataSet<D> dataset(DataKey<D> key) {
    return doGetDatasetFunction(key).apply(this);
  }

  private <D> Function<Fixture, DataSet<D>> doGetDatasetFunction(DataKey<D> key) {
    Function<Fixture, DataSet<D>> datasetFunction = registry.get(key);
    if (datasetFunction != null) {
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

  public <D> DataSetBuilder<D> newDataSet(DataKey<D> key) {
    return new DataSetBuilder<D>(key);
  }

  public <D> DataSetBuilder<D> newDataSet(Class<D> key) {
    return new DataSetBuilder<D>(DataKey.of(key));
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

  public class DataSetBuilder<D> {

    private DataKey<D> key;

    public DataSetBuilder(DataKey<D> key) {
      this.key = key;
    }

    // --- static datasets --- //
    public void composedOf(Iterable<D> data) {
      composedOf(ImmutableList.copyOf(data));
    }

    public void composedOf(D... items) {
      composedOf(ImmutableList.copyOf(items));
    }

    private void composedOf(ImmutableList<D> items) {
      DataSet<D> dataset = new DataSetImpl<D>(new StaticDataSupplier<D>(items, key.getType()));
      registry.register(key, Functions.constant(dataset));

    }

    // --- generated datasets --- //

    public void autoMagicallyGenerated(int numberOfItems) {

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(buildDynamicGenerator(key.getType()), Optional.of(numberOfItems))));

      registry.register(key, getDataset);

    }

    public void generatedBy(final Supplier<D> generator) {
      generatedBy(generator, Optional.<Integer> absent());
    }

    public void generatedBy(final Supplier<D> generator, final Integer numberOfItems) {
      generatedBy(generator, Optional.of(numberOfItems));

    }
    
    public void generatedBy(final GenerationStrategy<D> generator) {
      checkNotNull(generator, "generator argument is null");

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(generator,Optional.absent())));

      registry.register(key, getDataset);

    }

    protected void generatedBy(final Supplier<D> generator, final Optional<Integer> numberOfItems) {

      checkNotNull(generator, "generator argument is null");

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(toGenerationStrategy(generator), numberOfItems)));

      registry.register(key, getDataset);

    }

    public void generatedAsIterableBy(Supplier<Iterable<D>> generator) {
      checkNotNull(generator, "generator argument is null");

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(toGenerationStrategy(generator))));

      registry.register(key, getDataset);
    }

    private Function<Fixture, DataSupplier<D>> toDataSupplier(final GenerationStrategy<D> strategy, Optional<Integer> numberOfItems) {
      return fixture -> 
         new LazyGeneratedDataSupplier<>(
            key.getType(), 
            () -> strategy.generate(fixture),
            () -> numberOfItems.isPresent() ? numberOfItems.get() : strategy.size(fixture)
        );
      
    }

    private Function<Fixture, DataSupplier<D>> toDataSupplier(final GenerationStrategy<Iterable<D>> strategy) {
      return fixture -> new LazyGeneratedCollectionDataSupplier<>(() -> strategy.generate(fixture), key.getType());
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

      Function<Fixture, DataSet<D>> getDataset = cache(toDataSet(toDataSupplier(toGenerationStrategy(generator), numberOfItems)));

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
      return new DataSetImpl<>(dataSupplier.apply(fixture));
    };
  }

  private <X> GenerationStrategy<X> toGenerationStrategy(Supplier<X> generator) {
    return generationStrategyFactory.create(generator);
  }
  
  private <X> GenerationStrategy<X> buildDynamicGenerator(TypeToken<X> type) {
    return dynamicGeneratorFactory.buildGeneratorOf(type, FixtureFactory.this, dynamicGeneratorFactory).get();
  }

}