package org.magenta.core.automagic.generation.provider;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.core.GenerationStrategy;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;
import org.magenta.core.automagic.generation.provider.fields.mapper.DataSetFieldHydrater;
import org.magenta.core.automagic.generation.provider.fields.mapper.SequenceFieldHydrater;
import org.magenta.core.injector.FieldsExtractor;
import org.magenta.core.sequence.FieldSequenceDefinition;
import org.magenta.core.sequence.ObjectSequenceMap;
import org.magenta.core.sequence.ObjectSequenceMapBuilder;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class ObjectGeneratorFactory implements DynamicGeneratorFactory {

  private final FieldsExtractor fieldsExtractor;
  private final Function<Field,FieldSequenceDefinition> dataKeyDerivation;

  public ObjectGeneratorFactory(FieldsExtractor fieldsExtractor, Function<Field,FieldSequenceDefinition> dataKeyDerivation) {
    super();
    this.fieldsExtractor = fieldsExtractor;
    this.dataKeyDerivation = dataKeyDerivation;

  }

  @Override
  public <D> Optional<ObjectGenerator<D>> buildGeneratorOf(TypeToken<D> type, FixtureFactory fixture,
      DynamicGeneratorFactory dynamicGeneratorFactory) {

    if (!isApplicable(type)) {
      return Optional.absent();
    }

    Iterable<FieldSequenceDefinition> fieldSequenceDefinitions = buildDataKeyMapFor(type.getRawType());

    List<Field> ignoredFields = buildGeneratorForEachRequiredField(fixture, dynamicGeneratorFactory, fieldSequenceDefinitions);

    Function<Fixture, ObjectSequenceMap> sequenceProvider = CacheBuilder.newBuilder()
        .build(CacheLoader.from(new ObjectSequenceMapBuilder(
            Iterables.filter(fieldSequenceDefinitions, d->!ignoredFields.contains(d.getField()) && d.getType()==FieldSequenceDefinition.Type.ATTRIBUTE))));

    return Optional.of(new ObjectGenerator<D>(type, sequenceProvider, Arrays.asList(new SequenceFieldHydrater(sequenceProvider), new DataSetFieldHydrater())));
  }

  private boolean isApplicable(TypeToken<?> type) {
    return !type.getRawType().isEnum() && !type.getRawType().isArray() && !type.getRawType().isPrimitive() && hasNoArgsConstructor(type);
  }

  private boolean hasNoArgsConstructor(TypeToken<?> type) {
    return Stream.of(type.getRawType().getDeclaredConstructors()).anyMatch((c) -> c.getParameterCount() == 0);
  }

  private <D> Iterable<FieldSequenceDefinition> buildDataKeyMapFor(Class<D> type) {

    Iterable<FieldSequenceDefinition> definitions = FluentIterable
        .from(fieldsExtractor.extractAll(type))
        .transform(f -> dataKeyDerivation.apply(f));

    return definitions;
  }

  private List<Field> buildGeneratorForEachRequiredField(FixtureFactory fixture, DynamicGeneratorFactory dynamicGeneratorFactory,
      Iterable<FieldSequenceDefinition> definitions) {
    List<Field> toRemove = Lists.newArrayList();
    for (FieldSequenceDefinition d : definitions) {

      DataKey<?> key = d.getKey();
      if (!fixture.keys().contains(key)) {
        if (!addToFixtureIfPossible(key, fixture, dynamicGeneratorFactory)) {
          // remove it from the key map if it was not possible to generate a
          // dataset for this type
          toRemove.add(d.getField());
        }
      }
    }
    return toRemove;
  }

  private <X> boolean addToFixtureIfPossible(DataKey<X> key, FixtureFactory fixture, DynamicGeneratorFactory dynamicGeneratorFactory) {
    if (key.getType().getRawType().isEnum()) {
      addNewEnumDataSet(fixture, key);
      return true;
    } else {
      // TODO : in some case a dataset could be generated instead
      Optional<? extends GenerationStrategy<X>> nullableGenerator = dynamicGeneratorFactory.buildGeneratorOf(key.getType(), fixture, dynamicGeneratorFactory);
      if (nullableGenerator.isPresent()) {
        fixture.newGenerator(key).generatedBy(nullableGenerator.get());
        return true;
      } else {
        return false;
      }
    }
  }

  private <T> void addNewEnumDataSet(FixtureFactory fixture, DataKey<T> key) {
    fixture.newDataSet(key).composedOf(((Class<T>) key.getType().getRawType()).getEnumConstants());
  }

}
