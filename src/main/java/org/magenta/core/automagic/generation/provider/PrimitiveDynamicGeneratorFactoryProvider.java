package org.magenta.core.automagic.generation.provider;

import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;
import org.magenta.random.FluentRandom;

import com.google.common.reflect.TypeToken;
import com.google.inject.internal.ImmutableMap;

public class PrimitiveDynamicGeneratorFactoryProvider {

  private static Map<Class,Class> primitiveMap = ImmutableMap.<Class,Class>builder()
      .put(int.class, Integer.class)
      .put(short.class, Short.class)
      .put(double.class, Double.class)
      .put(float.class, Float.class)
      .put(long.class, Long.class)
      .build();
  
  public static List<DynamicGeneratorFactory> get(){
    List<DynamicGeneratorFactory> factories = Lists.newArrayList();
    
    factories.add(new ConditionalGeneratorFactory(type -> type.isAssignableFrom(String.class), () -> FluentRandom.strings().charabia(16)));
    factories.add(new ConditionalGeneratorFactory(type -> type.isAssignableFrom(Integer.class) || mappedType(type).isAssignableFrom(Integer.class), () -> FluentRandom.integers().anyPositive()));
    factories.add(new ConditionalGeneratorFactory(type -> type.isAssignableFrom(Long.class) || mappedType(type).isAssignableFrom(Long.class), () -> FluentRandom.longs().anyPositive()));
    factories.add(new ConditionalGeneratorFactory(type -> type.isAssignableFrom(Short.class) || mappedType(type).isAssignableFrom(Short.class), () -> FluentRandom.shorts().anyPositive()));
    factories.add(new ConditionalGeneratorFactory(type -> type.isAssignableFrom(Double.class) || mappedType(type).isAssignableFrom(Double.class), () -> FluentRandom.doubles().anyPositive()));
    factories.add(new ConditionalGeneratorFactory(type -> type.isAssignableFrom(Float.class) || mappedType(type).isAssignableFrom(Float.class), () -> new Float(FluentRandom.doubles().anyPositive())));
    
    return factories;
  }

  private static Class<?> mappedType(TypeToken<?> type) {
    
    Class mapped = primitiveMap.get(type.getRawType());
    if(mapped==null){
      mapped = Void.class;
    }
    return mapped;
  }
  
  
}
