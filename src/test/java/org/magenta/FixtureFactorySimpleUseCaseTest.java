package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class FixtureFactorySimpleUseCaseTest {

  @Test(expected = DataSetNotFoundException.class)
  public void testShouldThrowExceptionWhenTheDataSetIsNotFound() {
    // setup fixture
    FixtureFactory fixture = createRootFixtureFactory();

    // exercise sut
    fixture.dataset(Integer.class);

  }

  @Test
  public void testKeys() {
    // setup fixture
    FixtureFactory fixture = createRootFixtureFactory();
    fixture.newDataSet(String.class).composedOf("a","b","c");
    fixture.newDataSet(Integer.class).composedOf(1,2,3,4);

    // exercise sut
    assertThat(fixture.keys()).containsExactly(DataKey.of(String.class), DataKey.of(Integer.class));

  }

  @Test
  public void testNewChild(){
    // setup fixture
    FixtureFactory parent = createRootFixtureFactory();
    parent.newDataSet(String.class).composedOf("a","b","c");
    parent.newDataSet(Integer.class).composedOf(1,2,3,4);

    //exercise SUT
    FixtureFactory child = parent.newChild();

    //adding to child, so the parent should not have this dataset
    child.newDataSet(Double.class).composedOf(1.0,2.4,3.6);

    //verify outcome
    assertThat(child).isNotSameAs(parent);
    assertThat(parent.keys()).containsExactly(DataKey.of(String.class), DataKey.of(Integer.class));
    assertThat(child.keys()).containsExactly( DataKey.of(Double.class), DataKey.of(String.class), DataKey.of(Integer.class));

    //verify content of the parent
    assertThat(parent.dataset(String.class).array()).containsExactly("a","b","c");
    assertThat(parent.dataset(Integer.class).array()).containsExactly(1,2,3,4);

    //verify content of the child
    assertThat(child.dataset(String.class).array()).containsExactly("a","b","c");
    assertThat(child.dataset(Integer.class).array()).containsExactly(1,2,3,4);
    assertThat(child.dataset(Double.class).array()).containsExactly(1.0,2.4,3.6);


  }

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture();
  }
}
