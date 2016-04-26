package org.magenta.core.injector.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.Sequence;
import org.magenta.annotation.InjectSequence;
import org.magenta.core.Injector;
import org.magenta.core.injector.FieldInjectionHandler;
import org.magenta.core.injector.FieldInjectorUtils;
import org.magenta.core.injector.FieldsExtractor;
import org.magenta.core.sequence.FieldSequenceDefinition;
import org.magenta.core.sequence.ObjectSequenceMap;
import org.magenta.core.sequence.ObjectSequenceMapBuilder;
import org.magenta.core.sequence.SequenceForFixtureAdapter;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

public class SequenceFieldHandler extends AbstractFieldAnnotationHandler<InjectSequence> implements FieldInjectionHandler{


  public SequenceFieldHandler(FieldsExtractor extractors) {
    super(extractors);
  }

  @Override
  protected Class<InjectSequence> getAnnotationType(){
    return org.magenta.annotation.InjectSequence.class;
  }

  @Override
  public Map<Injector.Key<?>,Object> injectInto(Object target, Supplier<? extends Fixture> fixtureSupplier) {

    List<Field> allFields = fieldExtractors.extractAll(target.getClass());

    Set<FieldSequenceDefinition> entries = buildSequenceDefinitionSet(allFields);

    Function<Fixture, ObjectSequenceMap> sequenceProvider = buildSequenceProvider(entries);

    entries.forEach(e->injectDynamicSequenceIntoEachFieldOfTarget(e.getField(), target, fixtureSupplier, sequenceProvider));

    //throw invalidFieldType(f.getField());

    Function<Fixture,Integer> combinationCountFunction = f -> sequenceProvider.apply(f).getCombinationCount();

    Map<Injector.Key<?>, Object> injectionResults = ImmutableMap.of(Injector.Key.NUMBER_OF_COMBINATION_FUNCTION, combinationCountFunction);

    return injectionResults;
  }

  private Function<Fixture, ObjectSequenceMap> buildSequenceProvider(Set<FieldSequenceDefinition> entries) {
    Function<Fixture, ObjectSequenceMap> sequenceProvider = CacheBuilder.newBuilder().build(CacheLoader.from(new ObjectSequenceMapBuilder(entries)));
    return sequenceProvider;
  }

  private Set<FieldSequenceDefinition> buildSequenceDefinitionSet(List<Field> allFields) {
    Set<FieldSequenceDefinition> entries = FluentIterable.from(allFields)
        .filter(f->f.isAnnotationPresent(getAnnotationType()))
        .transform(f->getSequenceInfo(f, f.getAnnotation(getAnnotationType()))).toSet();
    return entries;
  }

  private FieldSequenceDefinition getSequenceInfo(Field f, InjectSequence annotation) {

    String qualifier = annotation.value();

    Type gt = f.getGenericType();
    if (gt instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) gt;
      Type t = pt.getActualTypeArguments()[0];
      if (t instanceof Class) {
        Class<?> keyType = ((Class<?>) t);

        return FieldSequenceDefinition.make(f, DataKey.of(qualifier, keyType), annotation.unique()? FieldSequenceDefinition.Type.UNIQUE_SEQUENCE :  FieldSequenceDefinition.Type.SEQUENCE);
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

  private IllegalStateException invalidFieldType(Field f) {
    return new IllegalStateException("Annotation "+InjectSequence.class.getName()+" is present on field named "+f.getName()+", but this field type is not a "+Sequence.class.getName());
  }


  /**
   * Create a dynamic sequence (one that get the sequence from the current fixture) and inject it into the specified field of the target object.
   *
   * @param f the field
   * @param target the target
   * @param fixtureSupplier the current fixture
   * @param sequenceProvider the sequence provider function
   */
  private void injectDynamicSequenceIntoEachFieldOfTarget(Field f, final Object target, final Supplier<? extends Fixture> fixtureSupplier,  Function<Fixture, ObjectSequenceMap> sequenceProvider) {

    Sequence<?> sequence = SequenceForFixtureAdapter.from(fixtureSupplier, sequenceForField(f, sequenceProvider) );

    FieldInjectorUtils.injectInto(target, f, sequence);
  }

  private <D> Function<Fixture, Sequence<D>> sequenceForField(final Field f, final Function<Fixture, ObjectSequenceMap> sequenceProvider) {
    return input -> (Sequence<D>) sequenceProvider.apply(input).get(f);
  }




}
