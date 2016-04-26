package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.magenta.testing.domain.company.Address;
import org.magenta.testing.domain.company.AddressGenerator;
import org.magenta.testing.domain.company.Contract;
import org.magenta.testing.domain.company.ContractGenerator;
import org.magenta.testing.domain.company.Employee;
import org.magenta.testing.domain.company.EmployeeGenerator;
import org.magenta.testing.domain.company.EmployeeGenerator2;
import org.magenta.testing.domain.company.EmployeeGenerator3;
import org.magenta.testing.domain.company.EmployeeGenerator4;
import org.magenta.testing.domain.company.Occupation;
import org.magenta.testing.domain.company.PhoneNumber;
import org.magenta.testing.domain.company.PhoneNumberGenerator;

import com.google.common.reflect.TypeToken;

public class FixtureFactoryGeneratedByTest {

  @Test
  public void testMissingSequenceForGenerator() {

    // setup fixtures

    FixtureFactory fixtures = createRootFixtureFactory();
    // exercise sut

    try {
      fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());
      DataSet<Employee> actual = fixtures.dataset(Employee.class);
      actual.any();
      fail("expecting"+DataGenerationException.class.getName());
    } catch (DataGenerationException dge) {
      dge.printStackTrace();
      assertThat(dge).hasMessageContaining("Employee").hasRootCauseInstanceOf(DataSetNotFoundException.class);
    }

  }


  @Test
  public void testAGeneratorUsingAPredeterminedInjectedSequence(){

    //setup fixtures
    Occupation[] expectedOccupation = new Occupation[]{Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER};

    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newDataSet(Occupation.class).composedOf(expectedOccupation);
    //exercise sut


    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator());
    DataSet<Employee> actual = fixtures.dataset(Employee.class);

    //verify outcome
    assertThat(actual.getType()).isEqualTo(TypeToken.of(Employee.class));
    assertThat(actual.list(4)).extracting("occupation").containsExactly(expectedOccupation);
  }

  @Test
  public void testAGeneratorUsingDifferentInjectedSequence(){

    //setup fixtures
    Occupation[] expectedOccupation = new Occupation[]{Occupation.ENGINEER, Occupation.MANAGEMENT, Occupation.TECHNICIAN, Occupation.TESTER};

    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newDataSet(Occupation.class).composedOf(expectedOccupation);
    fixtures.newGenerator(Address.class).generatedBy(new AddressGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator2());
    DataSet<Employee> actual = fixtures.dataset(Employee.class);

    //exercise sut and verify outcome

    assertThat(actual.getType()).isEqualTo(TypeToken.of(Employee.class));
    assertThat(actual.list(4)).extracting("occupation").containsExactly(expectedOccupation);
    assertThat(actual.list(4)).extracting("address").doesNotContainNull();
  }

  @Test
  public void testAGeneratorUsingAnInjectedDataSet(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.values());
    fixtures.newDataSet(Address.class).generatedBy(new AddressGenerator());
    fixtures.newGenerator(PhoneNumber.class).generatedBy(new PhoneNumberGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator3());
    DataSet<Employee> actual = fixtures.dataset(Employee.class);

    //exercise sut and verify outcome

    assertThat(actual.getType()).isEqualTo(TypeToken.of(Employee.class));
    assertThat(actual.any().getPhoneNumbers()).isNotEmpty();

  }

  @Test
  public void testAGeneratorUsingAnUniqueSequence(){
    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newDataSet(Employee.Id.class).transformed(id->Employee.Id.value((Long)id)).composedOf(1L,2L,3L);
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.values());
    fixtures.newDataSet(Address.class).generatedBy(new AddressGenerator());
    fixtures.newGenerator(PhoneNumber.class).generatedBy(new PhoneNumberGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator4());
    DataSet<Employee> actual = fixtures.dataset(Employee.class);

    //exercise sut and verify outcome
    assertThat(actual.getSize()).isEqualTo(3);

    actual.list().forEach(e->print(e));
  }

  @Test
  public void testAGeneratorUsingAnUniqueSequenceThatIsNotLimitedToASetOfValue(){
    //setup fixtures
    final AtomicLong idCount = new AtomicLong(0);

    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newDataSet(Employee.Id.class).transformed(id->Employee.Id.value((Long)id)).generatedBy(()->idCount.incrementAndGet());
    fixtures.newDataSet(Occupation.class).composedOf(Occupation.values());
    fixtures.newDataSet(Address.class).generatedBy(new AddressGenerator());
    fixtures.newGenerator(PhoneNumber.class).generatedBy(new PhoneNumberGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator4());
    DataSet<Employee> actual = fixtures.dataset(Employee.class);

    //exercise sut and verify outcome
    assertThat(actual.getSize()).isEqualTo(4);

    actual.list().forEach(e->print(e));
  }

  private void print(Object e) {
    System.out.println(e);
  }


  @Test
  public void testAGeneratorWithASpecifiedDefaultSize(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();

    //exercise sut
    fixtures.newDataSet(PhoneNumber.class).generatedBy(new PhoneNumberGenerator(), 20);
    DataSet<PhoneNumber> actual =fixtures.dataset(PhoneNumber.class);

    //verify outcome
    assertThat(actual.list()).hasSize(20);
  }

  @Test
  public void testAGeneratorWithASpecificSize(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();

    //exercise sut
    fixtures.newDataSet(PhoneNumber.class).generatedBy(new PhoneNumberGenerator(), 20);
    DataSet<PhoneNumber> actual =fixtures.dataset(PhoneNumber.class);

    //verify outcome
    assertThat(actual.list(30)).hasSize(30);
  }

  @Test
  public void testAGeneratorOfIterable(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();

    fixtures.newDataSetOf(Occupation.values());
    fixtures.newLazyDataSet(Address.class, new AddressGenerator());
    fixtures.newGenerator(PhoneNumber.class).generatedBy(new PhoneNumberGenerator());
    fixtures.newDataSet(Employee.class).generatedBy(new EmployeeGenerator3());
    fixtures.newDataSet(Contract.class).generatedAsIterableBy(new ContractGenerator());

    //exercise sut

    DataSet<Contract> actual =fixtures.dataset(Contract.class);
    DataSet<Employee> employees =fixtures.dataset(Employee.class);

    //verify outcome
    assertThat(actual.list()).extracting("employee").containsOnly(employees.array());
  }

  @Test
  public void testAFilteredGenerator(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();
    fixtures.newGenerator(PhoneNumber.class).generatedBy(new PhoneNumberGenerator(),3);


    //exercise sut

    DataSet<PhoneNumber> phones =fixtures.dataset(PhoneNumber.class);

    List<PhoneNumber> filteredPhones = phones.filter(phone -> phone.getPhoneNumber().startsWith("123")).list();

    //verify outcome
    assertThat(filteredPhones).hasSize(3);
  }

  @Test
  public void testATransformedGenerator(){

    //setup fixtures
    FixtureFactory fixtures = createRootFixtureFactory();

    PhoneNumber expected = new PhoneNumber();
    expected.setPhoneNumber("123-4567");

    fixtures.newDataSet(String.class).transformed((PhoneNumber p)->p.getPhoneNumber()).generatedBy(()->expected,3);


    //exercise sut

    DataSet<String> phones =fixtures.dataset(String.class);

    List<String> filteredPhones = phones.list();

    //verify outcome
    assertThat(filteredPhones).containsOnly("123-4567");
  }


  private FixtureFactory createRootFixtureFactory() {
    return Magenta.newFixture();
  }
}
