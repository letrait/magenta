package org.magenta.core.automagic.generation;

import java.lang.reflect.Field;
import java.util.List;

import org.magenta.DataGenerationException;
import org.magenta.core.injector.FieldsExtractor;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public class ReflexionBasedGeneratorFactory implements GeneratorFactory {

  private FluentRandom random;
  private FieldsExtractor fieldsExtractor;

  public ReflexionBasedGeneratorFactory(FluentRandom random, FieldsExtractor fieldsExtractor){
    this.random = random;
    this.fieldsExtractor = fieldsExtractor;
  }

  @Override
  public <D> Supplier<D> buildGeneratorOf(TypeToken<D> type){

    if(String.class.isAssignableFrom(type.getRawType())){
      return (Supplier<D>) stringGenerator();
    }else if(Integer.class.isAssignableFrom(type.getRawType())){
      return (Supplier<D>) integerGenerator();
    }else{
      return objectGenerator(type);
    }
  }

  private Supplier<Integer> integerGenerator() {
    return new Supplier<Integer>(){

      @Override
      public Integer get() {
        return random.integers().anyPositive(1000);
      }

    };
  }

  private Supplier<String> stringGenerator() {
    return new Supplier<String>(){

      @Override
      public String get() {
        return random.strings().charabia(8);
      }

    };
  }

  private <D> Supplier<D> objectGenerator(final TypeToken<D> type) {
    return new Supplier<D>(){

      @Override
      public D get() {

        try {
          D candidate = (D) type.getRawType().newInstance();


        List<Field> fields = fieldsExtractor.extractAll(type.getRawType());

        for(Field f:fields){
          Supplier<?> valueGenerator = buildGeneratorOf(TypeToken.of(f.getType()));
          f.setAccessible(true);
          f.set(candidate, valueGenerator.get());
        }

        return candidate;
        } catch (Exception e) {
          throw new DataGenerationException(String.format("Error while generating %s",type), e);
        }
      }

    };
  }
}
