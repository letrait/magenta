package org.magenta.core.automagic.generation.provider.fields.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Sequence;
import org.magenta.core.sequence.ObjectSequenceMap;
import org.mockito.Mockito;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class SequenceFieldHydraterTest {

  @Test
  public void testMappingToAnObjectWithoutFields() throws IllegalArgumentException, IllegalAccessException {
    //setup fixture
    FixtureFactory fixture = Mockito.mock(FixtureFactory.class);

    ObjectSequenceMap map = new ObjectSequenceMap(Maps.newHashMap(), 1);

    SequenceFieldHydrater sut = new SequenceFieldHydrater(Functions.constant(map));
    ClassWithoutFields candidate = new ClassWithoutFields();

    //exercise sut (no error thrown)
    sut.hydrate(candidate,fixture);
  }

  @Test
  public void hydrate_an_object_with_one_field_from_its_corresponding_sequence() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
    //setup fixture
    String expected = "generatedFromASequence";
    FixtureFactory fixture = Mockito.mock(FixtureFactory.class);

    Field aField = ClassWithOneField.class.getDeclaredField("aField");
    Sequence<String> sequence = new Sequence<String>(){

      @Override
      public String next() {
        return expected;
      }

      @Override
      public int size() {
        return 1;
      }

    };

    ObjectSequenceMap map = new ObjectSequenceMap(ImmutableMap.of(aField, sequence), 1);

    SequenceFieldHydrater sut = new SequenceFieldHydrater(Functions.constant(map));

    //exercise sut (no error thrown)
    ClassWithOneField candidate = new ClassWithOneField();
    sut.hydrate(candidate,fixture);

    //verify outcome
    assertThat(candidate.aField).isEqualTo(expected);

  }

  public static class ClassWithoutFields{

  }

  public static class ClassWithOneField{

    private String aField;



  }

}
