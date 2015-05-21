package org.magenta.core.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.magenta.Fixture;
import org.magenta.core.context.ThreadLocalFixtureContext;

public class ThreadLocalFixtureContextTest {

  @Test
  public void testShouldReturnNullWhenNoContext(){

    //setup fixture
    ThreadLocalFixtureContext sut = new ThreadLocalFixtureContext();

    //exercise sut


    //verify outcome
    assertThat(sut.get()).isNull();
    assertThat(sut.isSet()).isFalse();

  }

  @Test
  public void testShouldReturnCurrentFixtureWhenSett(){

    //setup fixture
    ThreadLocalFixtureContext sut = new ThreadLocalFixtureContext();
    Fixture expected = mock(Fixture.class);
    sut.set(expected);

    //verify outcome
    assertThat(sut.get()).isEqualTo(expected);

  }

  @Test
  public void testClearContext(){
    //setup fixture
    ThreadLocalFixtureContext sut = new ThreadLocalFixtureContext();
    Fixture expected = mock(Fixture.class);
    sut.set(expected);

    //exercise
    sut.clear();

    //verify outcome
    assertThat(sut.get()).isNull();
  }
}
