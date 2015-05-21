package org.magenta;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.magenta.core.DataSetFunctionRegistry;
import org.magenta.core.DataSetImpl;
import org.magenta.core.FixtureContext;
import org.magenta.core.Injector;
import org.magenta.core.RestrictionHelper;
import org.magenta.core.automagic.generation.GeneratorFactory;
import org.magenta.core.data.supplier.GeneratorDataSupplier;
import org.magenta.core.data.supplier.LazyGeneratedDataSupplier;
import org.magenta.core.data.supplier.StaticDataSupplier;
import org.magenta.core.data.supplier.SequenceWithFixtureContextManagementDecorator;
import org.magenta.core.sequence.SupplierSequenceAdapter;
import org.magenta.random.FluentRandom;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

public class FixtureFactory implements Fixture {

  private FixtureFactory parent;
  private FluentRandom random;
  private DataSetFunctionRegistry registry;
  private Injector injector;
  private GeneratorFactory generatorFactory;
  private FixtureContext context;

  FixtureFactory(FixtureFactory parent, FluentRandom random, Injector injector, GeneratorFactory generatorFactory,FixtureContext context) {
    this.parent = parent;
    this.random = random;
    this.injector = injector;
    this.context = context;
    this.registry = new DataSetFunctionRegistry();
    this.generatorFactory = generatorFactory;
  }

  public FixtureFactory newChild() {
    return new FixtureFactory(this, random, injector, generatorFactory, context);
  }

  @Override
  public <D> DataSet<D> dataset(Class<D> type) {
    return dataset(DataKey.of(type));
  }

  @Override
  @SuppressWarnings("unchecked")
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
  public FluentRandom getFluentRandom() {
    return this.random;
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
      DataSet<D> dataset = new DataSetImpl<D>(new StaticDataSupplier<D>(items, key.getType()), Suppliers.ofInstance(random));
      registry.register(key, Functions.constant(dataset));

    }

    // --- generated datasets --- //

    public void autoMagicallyGenerated(int numberOfItems) {
      generatedBy(buildGenerator(key.getType()),numberOfItems);

    }

    public void generatedBy(final Supplier<D> generator) {
      generatedBy(generator, LazyGeneratedDataSupplier.NO_SPECIFIED_DEFAULT_SIZE);
    }

    public void generatedBy(final Supplier<D> generator, final int numberOfItems) {
      registry.register(key,
          newLoadingCacheDecorator(
              newGeneratedDataSetFactory(
                  newSequenceAdapter(injector.inject(checkNotNull(generator,"generator argument is null"))
              ), numberOfItems)));

    }

    private Sequence<D> newSequenceAdapter(Supplier<D> generator) {
      return new SupplierSequenceAdapter<D>(generator);
    }

    private Function<Fixture, DataSet<D>> newLoadingCacheDecorator(Function<Fixture, DataSet<D>> function) {
      return CacheBuilder.newBuilder().build(CacheLoader.from(function));
    }

    private Function<Fixture, DataSet<D>> newGeneratedDataSetFactory(final Sequence<D> generator, final int numberOfItems) {
      return new Function<Fixture, DataSet<D>>() {

        @Override
        public DataSet<D> apply(Fixture fixture) {

          return new DataSetImpl<D>(new LazyGeneratedDataSupplier<>(contextualizeToFixture(fixture, generator), key.getType(),
              numberOfItems, Integer.MAX_VALUE), Suppliers.ofInstance(random));
        }

      };

    }

    private  Sequence<D> contextualizeToFixture(Fixture input, Sequence<D> generator) {
      // This datasupplier decorator will set the specified fixture as current
      // when generating data, so the generator will take their data seqence
      // from
      // that fixture
      return new SequenceWithFixtureContextManagementDecorator<D>(generator, input, context);
    }

    private Supplier<D> buildGenerator(TypeToken<D> type) {

      return generatorFactory.buildGeneratorOf(type);
    }


  }

  public class GeneratorBuilder<D> {

    private DataKey<D> key;

    public GeneratorBuilder(DataKey<D> key) {
      this.key = key;
    }

    public void generatedBy(final Supplier<D> generator) {
      generatedBy(generator, GeneratorDataSupplier.NO_SPECIFIED_DEFAULT_SIZE);
    }

    public void generatedBy(Supplier<D> generator, int defaultSize) {
      registry.register(key, newLoadingCacheDecorator(newParametizedBuilderOfGeneratedDataSet(injector.inject(generator), defaultSize)));

    }

    private Function<Fixture, DataSet<D>> newLoadingCacheDecorator(Function<Fixture, DataSet<D>> function) {
      return CacheBuilder.newBuilder().build(CacheLoader.from(function));
    }

    private Function<Fixture, DataSet<D>> newParametizedBuilderOfGeneratedDataSet(final Supplier<D> generator, final int numberOfItems) {
      return new Function<Fixture, DataSet<D>>() {

        @Override
        public DataSet<D> apply(Fixture fixture) {
          return new DataSetImpl<D>(new GeneratorDataSupplier<>(contextualizeGeneratorToFixture(fixture, adaptToSequence(generator)), key.getType(), numberOfItems,
              Integer.MAX_VALUE), Suppliers.ofInstance(random));
        }

      };

    }

    private Sequence<D> adaptToSequence(Supplier<D> generator) {
      return new SupplierSequenceAdapter<D>(generator);
    }

    private Sequence<D> contextualizeGeneratorToFixture(Fixture input, Sequence<D> generator) {
      // This datasupplier decorator will set the specified fixture as current
      // when generating data, so the generator will take their data seqence
      // from
      // that fixture
      return new SequenceWithFixtureContextManagementDecorator<D>(generator, input, context);
    }

  }


}