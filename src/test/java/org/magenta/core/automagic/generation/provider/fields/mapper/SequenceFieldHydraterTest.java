package org.magenta.core.automagic.generation.provider.fields.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.core.sequence.ObjectSequenceMapBuilder;

public class SequenceFieldHydraterTest {

  @Test
  public void testMappingToAnObjectWithoutFields() throws IllegalArgumentException, IllegalAccessException {
    //setup fixture
    FixtureFactory fixture = Magenta.newFixture();
    SequenceFieldHydrater sut = new SequenceFieldHydrater(Magenta.newSequenceMapBuilder(ClassWithoutFields.class));
    ClassWithoutFields candidate = new ClassWithoutFields();
    
    //exercise sut (no error thrown)
    sut.hydrate(candidate,fixture);
  }
  
  @Test
  public void hydrate_an_object_with_one_field_from_its_corresponding_sequence() throws IllegalArgumentException, IllegalAccessException{
    //setup fixture
    String expected = "generatedFromASequence";
    FixtureFactory fixture = Magenta.newFixture();
    fixture.newDataSet(String.class).composedOf(expected);
    
    ObjectSequenceMapBuilder sequenceMapBuilder = Magenta.newSequenceMapBuilder(ClassWithOneField.class);
    
    SequenceFieldHydrater sut = new SequenceFieldHydrater(sequenceMapBuilder);
   
    
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
