package org.magenta.core.sequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.FixtureFactory;
import org.magenta.Magenta;
import org.magenta.Sequence;
import org.magenta.core.sequence.SequenceIndexMap;
import org.magenta.core.sequence.SequenceProvider;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SequenceProviderTest {

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture(FluentRandom.singleton());
  }

  @Test
  public void testWhenNoFieldExistsThenNoSequencesAreProvided() {

    // setup fixture
    FixtureFactory fixture = createRootFixtureFactory();

    SequenceProvider sut = new SequenceProvider(Maps.<Field, DataKey<?>> newLinkedHashMap());

    // exercise sut
    SequenceIndexMap actual = sut.apply(fixture);

    // verify outcome
    assertThat(actual.isEmpty()).isTrue();

  }

  @Test
  public void test_when_a_datakey_refer_to_a_non_constant_dataset_the_resulting_sequence_is_undeterministic() throws NoSuchFieldException, SecurityException {

    // setup fixture
    FixtureFactory fixture = createRootFixtureFactory();

    //setup a string generator
    Supplier<String> stringGenerator = mock(Supplier.class);
    when(stringGenerator.get()).thenReturn("a", "b", "c");

    //the size of the generator is limited to 1, which would make the sequence return only "a" if the sequence is deterministic and "a" ,"b", "c"
    //if the sequence is undeterministic
    fixture.newGenerator(String.class).generatedBy(stringGenerator, 1);

    //map the field
    Map<Field, DataKey<?>> keyMap = Maps.newHashMap();

    Field stringField = DummyClass.class.getDeclaredField("string");
    keyMap.put(stringField, DataKey.of(String.class));

    //build sut
    SequenceProvider sut = new SequenceProvider(keyMap);

    // exercise sut
    Sequence<?> actual = sut.apply(fixture).get(stringField);

    // verify outcome
    List<String> actualSequence = readSequence(actual, 3);

    assertThat(actualSequence).containsExactly("a", "b", "c");

  }

  @Test
  public void test_when_a_datakey_refer_to_a_constant_dataset_the_resulting_sequence_is_coordinated() throws NoSuchFieldException, SecurityException{
    // setup fixture
    FixtureFactory fixture = createRootFixtureFactory();

    //setup a string generator
    Supplier<String> stringGenerator = mock(Supplier.class);
    when(stringGenerator.get()).thenReturn("a", "b", "c");

    //the size of the generator is limited to 2, which would make the sequence return only "a" and "b" if the sequence is deterministic and "a" ,"b", "c"
    //if the sequence is undeterministic
    fixture.newDataSet(String.class).generatedBy(stringGenerator, 2);

    //map the field
    Map<Field, DataKey<?>> keyMap = Maps.newHashMap();

    Field stringField = DummyClass.class.getDeclaredField("string");
    keyMap.put(stringField, DataKey.of(String.class));

    //build sut
    SequenceProvider sut = new SequenceProvider(keyMap);

    // exercise sut
    Sequence<?> actual = sut.apply(fixture).get(stringField);

    // verify outcome
    assertThat(actual).as("the sequence").isNotNull();
    List<String> actualSequence = readSequence(actual, 4);

    assertThat(actualSequence).containsExactly("a", "b", "a", "b");
  }

  private List<String> readSequence(Sequence<?> actual, int size) {
    List<String> actualSequence = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      actualSequence.add((String) actual.get());
    }
    return actualSequence;
  }

  public class DummyClass {
    String string;
  }
}
