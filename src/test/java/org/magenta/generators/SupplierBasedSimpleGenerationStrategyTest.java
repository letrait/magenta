package org.magenta.generators;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataSpecification;
import org.magenta.SimpleDataSpecification;

import com.google.common.base.Supplier;

public class SupplierBasedSimpleGenerationStrategyTest {

	@Test
	public void testGenerateItem(){
		//setup fixtures

		DataDomain<DataSpecification> dataDomain=mock(DataDomain.class);

		String[] expected = new String[]{"generated string"};

		Supplier<String> stringSupplier=mock(Supplier.class);

		when(stringSupplier.get()).thenReturn(expected[0]);

		SupplierBasedSimpleGenerationStrategyAdapter<String> sut=new SupplierBasedSimpleGenerationStrategyAdapter<>(stringSupplier,1);

		//exercise sut
		Iterable<String> actual = sut.generate(dataDomain);

		//verify outcome
		assertThat(actual).containsOnly(expected);

	}

	@Test
	public void testGeneratePreferredNumberOfItems(){
		//setup fixtures

		DataDomain<DataSpecification> dataDomain=mock(DataDomain.class);

		when(dataDomain.getSpecification()).thenReturn(SimpleDataSpecification.create());

		String[] expected = new String[]{"1","2","3","4","5","6","7","8","9","10"};

		Supplier<String> stringSupplier=mock(Supplier.class);

		when(stringSupplier.get()).thenReturn(expected[0],expected[1],expected[2],expected[3],expected[4],expected[5],expected[6],expected[7],expected[8],expected[9]);

		SupplierBasedSimpleGenerationStrategyAdapter<String> sut=new SupplierBasedSimpleGenerationStrategyAdapter<>(stringSupplier);

		//exercise sut
		Iterable<String> actual = sut.generate(dataDomain);

		//verify outcome
		assertThat(actual).containsOnly(expected);

	}
}
