package org.magenta.core.automagic.generation;

import java.lang.reflect.Field;
import java.util.Map;

import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.core.DataKeyMapBuilder;
import org.magenta.core.injector.FieldsExtractor;
import org.magenta.core.sequence.SequenceIndexMap;
import org.magenta.core.sequence.SequenceProvider;
import org.magenta.random.FluentRandom;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.reflect.TypeToken;

public class ReflexionBasedGeneratorFactory implements GeneratorFactory {

  private final DataKeyMapBuilder dataKeyMapBuilder;
  private final FieldsExtractor fieldsExtractor;
  private final Supplier<? extends Fixture> fixtureSupplier;

  public ReflexionBasedGeneratorFactory( Supplier<? extends Fixture> fixtureSupplier,  DataKeyMapBuilder dataKeyMapBuilder, FieldsExtractor fieldsExtractor) {
    this.fieldsExtractor = fieldsExtractor;
    this.fixtureSupplier = fixtureSupplier;
    this.dataKeyMapBuilder = dataKeyMapBuilder;
  }

  @Override
  public <D> Supplier<D> buildGeneratorOf(TypeToken<D> type, FixtureFactory fixture) {
    if(type.getRawType().isEnum()){
      throw new UnsupportedOperationException(String.format("Generating enum type is not supported : %s",type));
    }else if (int.class.isAssignableFrom(type.getRawType())) {
      return (Supplier<D>) integerGenerator();
    } else if (long.class.isAssignableFrom(type.getRawType())) {
      return (Supplier<D>) longGenerator();
   }else if (String.class.isAssignableFrom(type.getRawType())) {
      return (Supplier<D>) stringGenerator();
    } else if (Integer.class.isAssignableFrom(type.getRawType())) {
      return (Supplier<D>) integerGenerator();
    } else if (Long.class.isAssignableFrom(type.getRawType())) {
        return (Supplier<D>) longGenerator();
    } else if (Iterable.class.isAssignableFrom(type.getRawType())) {
      return (Supplier<D>) Suppliers.ofInstance(null);
   } else {
      return objectGenerator(type, fixture);
    }
  }

  private Supplier<Integer> integerGenerator() {
    return new Supplier<Integer>() {
      @Override
      public Integer get() {
        return FluentRandom.integers().anyPositive();
      }

    };
  }

  private Supplier<String> stringGenerator() {
    return new Supplier<String>() {

      @Override
      public String get() {
        return FluentRandom.strings().charabia(8);
      }

    };
  }

  private Supplier<Long> longGenerator() {
    return new Supplier<Long>() {
      @Override
      public Long get() {
        return FluentRandom.longs().anyPositive();
      }

    };
  }

  private <D> Supplier<D> objectGenerator(final TypeToken<D> type, FixtureFactory fixture) {

    Map<Field, DataKey<?>> keyMap = dataKeyMapBuilder.buildMapFrom(fieldsExtractor.extractAll(type.getRawType()));

    for(Map.Entry<Field, DataKey<?>> e:keyMap.entrySet()){
     if(!fixture.keys().contains(e.getValue())){
       if(e.getKey().getType().isEnum()){
         fixture.newDataSet((DataKey)e.getValue()).composedOf(e.getKey().getType().getEnumConstants());
       }else{
         //TODO : in some case a dataset could be generated instead
         fixture.newGenerator((DataKey)e.getValue()).generatedBy(buildGeneratorOf(TypeToken.of(e.getKey().getGenericType()),fixture));
       }
     }
    }

    Function<Fixture, SequenceIndexMap> sequenceProvider = CacheBuilder.newBuilder().build(CacheLoader.from(new SequenceProvider(keyMap)));

    return new ObjectGenerator<D>(type, fixtureSupplier, sequenceProvider);
  }
}
