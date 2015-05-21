package org.magenta.core.sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import org.junit.Test;
import org.magenta.Sequence;
import org.magenta.core.sequence.SequenceIndexMap;

public class SequenceIndexMapTest {

  String aField;

  @Test
  public void testPutGet() throws NoSuchFieldException, SecurityException{

    //setup fixture

    Field field = SequenceIndexMapTest.class.getDeclaredField("aField");
    Sequence<?> expected = mock(Sequence.class);

    SequenceIndexMap sut = new SequenceIndexMap();
    sut.put(field,expected);

    //exercise sut
    Sequence<?> actual = sut.get(field);

    //verify outcome
    assertThat(actual).isEqualTo(expected);

  }

}
