package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.magenta.events.DataGenerated;
import org.magenta.events.DataSetFound;
import org.magenta.testing.domain.company.Employee;
import org.magenta.testing.domain.company.EmployeeGenerator;
import org.magenta.testing.domain.company.Occupation;

import com.google.common.eventbus.Subscribe;
import com.google.inject.internal.Lists;

public class FixtureFactoryEventPostingTest {

  private List<Object> events = Lists.newArrayList();

  @Before
  public void registerAslistener(){
    Magenta.eventBus().register(this);
  }

  @After
  public void unregisterAslistener(){
    Magenta.eventBus().unregister(this);
  }

  @Test
  public void testDataSetFoundPosting(){

    //setup
    FixtureFactory fixtures = createRootFixtureFactory();

    fixtures.newDataSet(Integer.class).composedOf(1,2,3,4,5);

    DataSetFound expected = DataSetFound.from(DataKey.of(Integer.class), fixtures);

    //test

    fixtures.dataset(Integer.class).list();

    //verify
    assertThat(events).contains(expected);
  }

  @Test
  public void testDataGeneratedPosting(){

    //setup
    FixtureFactory fixtures = createRootFixtureFactory();

    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.values());

    //test

    Employee generated = fixtures.dataset(Employee.class).first();

    DataGenerated expected = DataGenerated.of(DataKey.of(Employee.class), generated, fixtures);

    //verify
    assertThat(events).contains(expected);
  }

  @Test
  public void testDataGeneratedPostingFromChild(){

    //setup
    FixtureFactory fixtures = createRootFixtureFactory();

    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.values());

    //test

    Employee generated = fixtures.restrictTo(Occupation.ENGINEER).dataset(Employee.class).first();

    DataGenerated expected = DataGenerated.of(DataKey.of(Employee.class), generated, fixtures);

    //verify
    assertThat(events).contains(expected);
  }

  @Subscribe
  public void receive(Object event){
    System.out.println(event);
    events.add(event);
  }

  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture();
  }

}
