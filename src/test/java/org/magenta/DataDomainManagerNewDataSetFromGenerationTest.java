package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.magenta.testing.MagentaAssertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.base.Supplier;

public class DataDomainManagerNewDataSetFromGenerationTest extends FixtureFactoryTestSupport {

  @Test
  public void testGeneratedBySupplier(){

    //setup fixtures
    FixtureFactory<SimpleDataSpecification> sut = createAnonymousFixtureFactory();
    Supplier<String> colorSupplier = mock(Supplier.class);

    when(colorSupplier.get()).thenReturn("red", "blue", "green","black");

    //exercise sut
    DataSet<String> colors = sut.newDataSet(String.class).generatedBy(colorSupplier,3);

    //verify outcome
    assertThat(sut).theDataSet(String.class).isNotNull().isEqualTo(colors).containsExactly("red","blue","green");


  }

  @Test
  public void testGeneratedBySupplier_default_number_of_generated_item(){

    //setup fixtures
    FixtureFactory<SimpleDataSpecification> sut = createAnonymousFixtureFactory();
    Supplier<Integer> colorSupplier = mock(Supplier.class);

    when(colorSupplier.get()).thenReturn(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15);

    //exercise sut
    DataSet<Integer> colors = sut.newDataSet(Integer.class).generatedBy(colorSupplier);

    //verify outcome
    //the default number of item to generate is specified by the data specification which is 10 by default for this test
    assertThat(sut.getSpecification().getDefaultNumberOfItems()).isEqualTo(10);
    assertThat(sut).theDataSet(Integer.class).isNotNull().isEqualTo(colors).containsExactly(1,2,3,4,5,6,7,8,9,10);


  }

  @Test
  public void testGeneratedByImplicitGenerationStrategy(){

    //setup fixtures
    FixtureFactory<SimpleDataSpecification> sut = createAnonymousFixtureFactory();
    Supplier<Iterable<String>> colorSupplier = mock(Supplier.class);

    when(colorSupplier.get()).thenReturn(Arrays.asList("red", "blue", "green"));

    //exercise sut
    DataSet<String> colors = sut.newDataSet(String.class).generatedAsIterableBy(colorSupplier);

    //verify outcome
    assertThat(sut).theDataSet(String.class).isNotNull().isEqualTo(colors).containsExactly("red","blue","green");


  }



}
