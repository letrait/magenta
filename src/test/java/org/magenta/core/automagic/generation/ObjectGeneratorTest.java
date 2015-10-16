package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.DataKeyMapBuilder;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.magenta.core.sequence.SequenceIndexMap;
import org.magenta.core.sequence.SequenceProvider;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class ObjectGeneratorTest {

  @Test
  public void testGenerationOfAnObjectWithoutField(){

    //setup fixture
    TypeToken<DummyObject> type = TypeToken.of(DummyObject.class);
    FixtureFactory fixture = Magenta.newFixture();

    Function<Fixture, SequenceIndexMap> sequenceProvider = createSequenceProviderFrom(type);

    ObjectGenerator<DummyObject> sut = new ObjectGenerator<>( type ,Suppliers.ofInstance(fixture), sequenceProvider);

    //exercise sut
    DummyObject actual = sut.get();

    //verify outcome
    assertThat(actual).isNotNull();
  }



  @Test
  public void testGenerationOfAnObjectWithAnEnum(){

    //setup fixture
    TypeToken<DummyObjectWithEnum> type = TypeToken.of(DummyObjectWithEnum.class);
    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(DataKey.of(DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.values());
    fixture.newDataSet(DataKey.of("org.magenta.core.automagic.generation.ObjectGeneratorTest$DummyObjectWithEnum.color", DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.RED);


    Function<Fixture, SequenceIndexMap> sequenceProvider = createSequenceProviderFrom(type);

    ObjectGenerator<DummyObjectWithEnum> sut = new ObjectGenerator<>(type, Suppliers.ofInstance(fixture), sequenceProvider);

    //exercise sut
    List<DummyObjectWithEnum> actual = read(sut,3);


    //verify outcome
    assertThat(actual).doesNotContainNull().extracting("color").containsExactly(
        DummyObjectWithEnum.Color.RED,
        DummyObjectWithEnum.Color.GREEN,
        DummyObjectWithEnum.Color.BLUE);

  }

  private Function<Fixture, SequenceIndexMap> createSequenceProviderFrom(TypeToken<?> type) {

    return CacheBuilder.newBuilder().build(CacheLoader.from(
        new SequenceProvider(
            new DataKeyMapBuilder(
                new DataKeyDeterminedFromFieldTypeMappingFunction()).buildMapFrom(HiearchicalFieldsExtractor.SINGLETON.extractAll(type.getRawType())))));
  }

  private <D> List<D> read(ObjectGenerator<D> sut, int size) {
    List<D> objects = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      objects.add(sut.get());
    }
    return objects;
  }

  public static class DummyObject{

  }

  public static class DummyObjectWithEnum{

    public enum Color {
      RED, GREEN, BLUE;
    }

    private Color color;

  }
}
