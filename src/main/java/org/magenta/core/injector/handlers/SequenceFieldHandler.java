package org.magenta.core.injector.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.Sequence;
import org.magenta.annotation.InjectSequence;
import org.magenta.core.injector.FieldInjectionHandler;
import org.magenta.core.injector.FieldInjectorUtils;
import org.magenta.core.injector.FieldsExtractor;
import org.magenta.core.sequence.SequenceForFixtureAdapter;
import org.magenta.core.sequence.SequenceIndexMap;
import org.magenta.core.sequence.SequenceProvider;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;

public class SequenceFieldHandler extends AbstractFieldAnnotationHandler<InjectSequence> implements FieldInjectionHandler{

  public SequenceFieldHandler(FieldsExtractor extractors) {
    super(extractors);
  }

  @Override
  protected Class<InjectSequence> getAnnotationType(){
    return org.magenta.annotation.InjectSequence.class;
  }

  @Override
  public void injectInto(Object target,Supplier<? extends Fixture> fixture) {

    Map<Field, DataKey<?>> keyMap = Maps.newLinkedHashMap();
    Function<Fixture, SequenceIndexMap> sequenceProvider = CacheBuilder.newBuilder().build(CacheLoader.from(new SequenceProvider(keyMap)));

    for (FieldAnnotation<InjectSequence> f : matchingFields(target)) {
      if (Sequence.class.equals(f.getField().getType())) {
        keyMap.put(f.getField(), determinDataKeyFromGenericType(f.getField(), f.getAnnotation()));
        injectProxySequence(f.getField(), target, fixture, sequenceProvider);
      } else {
        throw invalidFieldType(f.getField());
      }

    }
  }

  private IllegalStateException invalidFieldType(Field f) {
    return new IllegalStateException("Annotation "+InjectSequence.class.getName()+" is present on field named "+f.getName()+", but this field type is not a "+Sequence.class.getName());
  }


  private void injectProxySequence(Field f, final Object target, final Supplier<? extends Fixture> fixtureSupplier,  Function<Fixture, SequenceIndexMap> sequenceProvider) {

    Sequence<?> sequence = SequenceForFixtureAdapter.from(fixtureSupplier, sequenceForField(f, sequenceProvider) );

    FieldInjectorUtils.injectInto(target, f, sequence);
  }

  private <D> Function<Fixture, Sequence<D>> sequenceForField(final Field f, final Function<Fixture, SequenceIndexMap> sequenceProvider) {
   return new Function<Fixture, Sequence<D>>(){

    @Override
    public Sequence<D> apply(Fixture input) {
      return (Sequence<D>) sequenceProvider.apply(input).get(f);
    }

   };
  }

  private <T> DataKey<T> determinDataKeyFromGenericType(Field f, InjectSequence annotation) {

    String qualifier = annotation.value();

    Type gt = f.getGenericType();
    if (gt instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) gt;
      Type t = pt.getActualTypeArguments()[0];
      if (t instanceof Class) {
        Class<T> keyType = ((Class<T>) t);

        return DataKey.of(qualifier, keyType);
      } else {
        throw new IllegalStateException("Dataset cannot be injected into field [" + f.getName() + "] of [" + f.getDeclaringClass()
            .getName() + "] because the specified DataSet is a generic type [" + t
            + "].  A specific type should be declared such as DataSet<Integer>  instead of DataSet<D>.");
      }
    } else {
      throw new IllegalStateException(
          "Dataset cannot be injected into field ["
              + f.getName()
              + "] of ["
              + f.getDeclaringClass()
                  .getName()
              + "] because the key cannot be derived from the DataSet since it is a rawtype.  A specific type should be declared such as a DataSet<Integer> instead of just a raw DataSet.");
    }

  }


}
