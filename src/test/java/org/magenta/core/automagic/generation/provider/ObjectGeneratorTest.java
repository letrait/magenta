package org.magenta.core.automagic.generation.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.automagic.generation.DataKeyDeterminedFromFieldTypeMappingFunction;
import org.magenta.core.automagic.generation.DynamicGeneratorFactory;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class ObjectGeneratorTest {

  private  ObjectGeneratorFactory factory;
  private DynamicGeneratorFactory dynamicGeneratorFactoryMock;

  @Before
  public void setupObjectGeneratorFactory(){

    factory = new ObjectGeneratorFactory(HiearchicalFieldsExtractor.SINGLETON, new DataKeyDeterminedFromFieldTypeMappingFunction());
    dynamicGeneratorFactoryMock = Mockito.mock(DynamicGeneratorFactory.class);
  }

  @Test
  public void testGenerationOfAnObjectWithoutField(){

    //setup fixture
    DataKey<DummyObject> type = DataKey.of(DummyObject.class);
    FixtureFactory fixture = Magenta.newFixture();

    ObjectGenerator<DummyObject> sut = factory.buildGeneratorOf(type, fixture, dynamicGeneratorFactoryMock).get();

    //exercise sut
    DummyObject actual = sut.generate(fixture);

    //verify outcome
    assertThat(actual).isNotNull();
  }


  @Test
  public void testGenerationOfAnObjectWithAnEnum(){

    //setup fixture
    DataKey<DummyObjectWithEnum> type = DataKey.of(DummyObjectWithEnum.class);
    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(DataKey.of(DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.values());
    fixture.newDataSet(DataKey.of("org.magenta.core.automagic.generation.provider.ObjectGeneratorTest$DummyObjectWithEnum.color", DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.RED);

    ObjectGenerator<DummyObjectWithEnum> sut = factory.buildGeneratorOf(type, fixture, dynamicGeneratorFactoryMock).get();

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
    DataKey<DummyObjectWithEnum> type = DataKey.of(DummyObjectWithEnum.class);
    FixtureFactory fixture = Magenta.newFixture();

    fixture.newDataSet(DataKey.of(DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.values());
    fixture.newDataSet(DataKey.of("org.magenta.core.automagic.generation.provider.ObjectGeneratorTest$DummyObjectWithEnum.color", DummyObjectWithEnum.Color.class)).composedOf(DummyObjectWithEnum.Color.RED);


    ObjectGenerator<DummyObjectWithEnum> sut = factory.buildGeneratorOf(type, fixture, dynamicGeneratorFactoryMock).get();

    //exercise sut
    Integer actual = sut.size(fixture);


    //verify outcome

    assertThat(actual).isEqualTo(DummyObjectWithEnum.Color.values().length);

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
