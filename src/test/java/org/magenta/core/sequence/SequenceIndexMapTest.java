package org.magenta.core.sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.magenta.Sequence;

import com.google.inject.internal.Maps;

public class SequenceIndexMapTest {

  String aField;

  @Test
  public void testPutGet() throws NoSuchFieldException, SecurityException{

    //setup fixture

    Field field = SequenceIndexMapTest.class.getDeclaredField("aField");
    Sequence<?> expected = mock(Sequence.class);
   
    
    Map<Field,Sequence<?>> map = Maps.newHashMap();
    map.put(field,expected);

    ObjectSequenceMap sut = new ObjectSequenceMap(map,0);
    

    //exercise sut
    Sequence<?> actual = sut.get(field);

    //verify outcome
    assertThat(actual).isEqualTo(expected);

  }

  @Test
  public void testFields() throws NoSuchFieldException, SecurityException{

    //setup fixture

    Field field = SequenceIndexMapTest.class.getDeclaredField("aField");
    Sequence<?> expected = mock(Sequence.class);
    
    Map<Field,Sequence<?>> map = Maps.newHashMap();
    map.put(field,expected);

    ObjectSequenceMap sut = new ObjectSequenceMap(map,0);


    //exercise sut
    Set<Field> actual = sut.fields();

    //verify outcome
    assertThat(actual).containsExactly(field);

  }
  
  @Test
  public void testGetCombinationCount() throws NoSuchFieldException, SecurityException{

    //setup fixture
    Integer expectedCount = 4;
    Map<Field,Sequence<?>> map = Maps.newHashMap();

    ObjectSequenceMap sut = new ObjectSequenceMap(map, expectedCount);


    //exercise sut
    Integer actual = sut.getCombinationCount();

    //verify outcome
    assertThat(actual).isEqualTo(expectedCount);

  }

}
