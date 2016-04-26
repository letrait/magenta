package org.magenta.core.automagic.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.core.sequence.FieldSequenceDefinition;

public class DataKeyDeterminedFromFieldTypeMappingFunctionTest {

  @Test
  public void testMappingOfSimpleField() throws NoSuchFieldException, SecurityException {

    // setup fixture
    DataKeyDeterminedFromFieldTypeMappingFunction sut = new DataKeyDeterminedFromFieldTypeMappingFunction();
    DataKey<String> k = DataKey.of("org.magenta.core.automagic.generation.DataKeyDeterminedFromFieldTypeMappingFunctionTest$FakeObject.string",
        String.class);
    FieldSequenceDefinition expected = FieldSequenceDefinition.make(FakeObject.class.getDeclaredField("string"), k,
        FieldSequenceDefinition.Type.ATTRIBUTE);

    // exercise sut
    FieldSequenceDefinition actual = sut.apply(FakeObject.class.getDeclaredField("string"));

    // verify outcome
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void testMappingOfListField() throws NoSuchFieldException, SecurityException {

    // setup fixture
    DataKeyDeterminedFromFieldTypeMappingFunction sut = new DataKeyDeterminedFromFieldTypeMappingFunction();
    DataKey<String> k = DataKey.of("org.magenta.core.automagic.generation.DataKeyDeterminedFromFieldTypeMappingFunctionTest$FakeObject.strings",
        String.class);
    FieldSequenceDefinition expected = FieldSequenceDefinition.make(FakeObject.class.getDeclaredField("strings"), k,
        FieldSequenceDefinition.Type.ITERABLE);

    // exercise sut
    FieldSequenceDefinition actual = sut.apply(FakeObject.class.getDeclaredField("strings"));

    // verify outcome
    assertThat(actual).isEqualTo(expected);

  }

  public static class FakeObject {
    private String string;
    private List<String> strings;
  }
}
