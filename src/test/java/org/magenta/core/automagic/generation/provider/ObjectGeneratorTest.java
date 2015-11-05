package org.magenta.core.automagic.generation.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.DataKeyMapBuilder;
import org.magenta.core.automagic.generation.DataKeyDeterminedFromFieldTypeMappingFunction;
import org.magenta.core.automagic.generation.provider.ObjectGenerator;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.magenta.core.sequence.ObjectSequenceMap;
import org.magenta.core.sequence.ObjectSequenceMapBuilder;

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

    Function<Fixture, ObjectSequenceMap> sequenceProvider = createSequenceProviderFrom(type);

    ObjectGenerator<DummyObject> sut = new ObjectGenerator<>( type , sequenceProvider);

    //exercise sut
    DummyObject actual = sut.generate(fixture);

    //verify outcome
    assertThat(actual).isNotNull();
  }



  @Test
  public void testGenerationOfAnObjectWithAnEnum(){

    //setup fixture
    TypeToken<DummyObjectWithEnum> type = TypeToken.of(DummyObjectWithEnum.class);
    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(DataKey.of(DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.values());
    fixture.newDataSet(DataKey.of("org.magenta.core.automagic.generation.provider.ObjectGeneratorTest$DummyObjectWithEnum.color", DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.RED);


    Function<Fixture, ObjectSequenceMap> sequenceProvider = createSequenceProviderFrom(type);

    ObjectGenerator<DummyObjectWithEnum> sut = new ObjectGenerator<>(type, sequenceProvider);

    //exercise sut
    List<DummyObjectWithEnum> actual = read(sut,3, fixture);


    //verify outcome
 
    assertThat(actual).doesNotContainNull().extracting("color").containsExactly(
        DummyObjectWithEnum.Color.RED,
        DummyObjectWithEnum.Color.GREEN,
        DummyObjectWithEnum.Color.BLUE);

  }
  
  @Test
  public void testSizeOfAnObjectWithAnEnum(){

    //setup fixture
    TypeToken<DummyObjectWithEnum> type = TypeToken.of(DummyObjectWithEnum.class);
    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(DataKey.of(DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.values());
    fixture.newDataSet(DataKey.of("org.magenta.core.automagic.generation.provider.ObjectGeneratorTest$DummyObjectWithEnum.color", DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.RED);


    Function<Fixture, ObjectSequenceMap> sequenceProvider = createSequenceProviderFrom(type);

    ObjectGenerator<DummyObjectWithEnum> sut = new ObjectGenerator<>(type, sequenceProvider);

    //exercise sut
    Integer actual = sut.size(fixture);


    //verify outcome
 
    assertThat(actual).isEqualTo(DummyObjectWithEnum.Color.values().length);

  }

  private Function<Fixture, ObjectSequenceMap> createSequenceProviderFrom(TypeToken<?> type) {

    return CacheBuilder.newBuilder().build(CacheLoader.from(
        new ObjectSequenceMapBuilder(
            new DataKeyMapBuilder(
                new DataKeyDeterminedFromFieldTypeMappingFunction()).buildMapFrom(HiearchicalFieldsExtractor.SINGLETON.extractAll(type.getRawType())))));
  }

  private <D> List<D> read(ObjectGenerator<D> sut, int size,Fixture fixture) {
    List<D> objects = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      objects.add(sut.generate(fixture));
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
