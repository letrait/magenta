package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class DataKeyMapBuilderTest {

  @Test
  public void testAllFieldsAreMappedToDataKeyUsingTheMappingFunction(){

    //setup fixture
    DataKey<Object> expected = DataKey.of("DataKeyMapBuilderTest", Object.class);
    Function<Object,DataKey<Object>> mappingFunction = Functions.constant(expected);

    DataKeyMapBuilder sut = new DataKeyMapBuilder((Function)mappingFunction);
    List<Field> fields = HiearchicalFieldsExtractor.SINGLETON.extractAll(FakeObject.class);

    //exercise sut
    Map<Field,DataKey<?>> actual = sut.buildMapFrom(fields);

    //verify outcome
    assertThat(actual).isNotNull();
    assertThat(actual.keySet()).containsAll(fields);
    assertThat(actual.values()).containsOnly(expected);

  }

  @Test
  public void testIterableFieldsAreNotMappedToDataKey(){

    //setup fixture
    DataKey<Object> expected = DataKey.of("DataKeyMapBuilderTest", Object.class);
    Function<Object,DataKey<Object>> mappingFunction = Functions.constant(expected);

    DataKeyMapBuilder sut = new DataKeyMapBuilder((Function)mappingFunction);
    List<Field> fields = HiearchicalFieldsExtractor.SINGLETON.extractAll(FakeObjectWithIterables.class);

    //exercise sut
    Map<Field,DataKey<?>> actual = sut.buildMapFrom(fields);

    //verify outcome
    assertThat(actual).isNotNull();
    assertThat(actual.keySet()).containsAll(fields);
    assertThat(actual.values()).containsOnly(expected);
  }

  @SuppressWarnings("unused")
  public static class FakeObject {

    private String field1;
    private Object field2;
    private int field3;
  }

  @SuppressWarnings("unused")
  public static class FakeObjectWithIterables {

    private List<Integer> integers;
    private Collection<Date> dates;
    private Set<Object> objects;
    private Iterable<String> strings;
  }
}
