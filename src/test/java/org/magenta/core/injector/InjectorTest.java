package org.magenta.core.injector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.magenta.Fixture;
import org.magenta.Sequence;
import org.magenta.annotation.InjectSequence;
import org.magenta.core.Injector;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;
import org.magenta.core.injector.handlers.SequenceFieldHandler;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class InjectorTest {

  @Test
  public void test_sequence_injection(){

    //setup fixtures
    Fixture fixture = mock(Fixture.class);
    Supplier<Fixture> fixtureReference = Suppliers.ofInstance(fixture);
    Injector injector = createInjector(fixtureReference);

    DummyObject sut = new DummyObject();

    //exercise sut
    Map<Injector.Key<?>,Object> map = injector.inject(sut);

    //verify outcome
    assertThat(sut.getSequence()).isNotNull();
    assertThat(map).containsKey(Injector.Key.NUMBER_OF_COMBINATION_FUNCTION);
  }

  private Injector createInjector(Supplier<Fixture> fixtureReference) {

    List<FieldInjectionHandler> handlers = new ArrayList<>();

    handlers.add(sequenceFieldHandler());

    return new FieldInjectionChainProcessor(handlers, fixtureReference);
  }

  private SequenceFieldHandler sequenceFieldHandler() {
     return new SequenceFieldHandler(HiearchicalFieldsExtractor.SINGLETON);
  }

  public class DummyObject {

    @InjectSequence
    private Sequence<String> strings;

    public Sequence<String> getSequence(){
      return strings;
    }
  }
}
