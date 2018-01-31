package org.magenta.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.Fixture;


public class SupplierBasedSimpleGenerationStrategyTest {



	@Test
	public void testGeneratePreferredNumberOfItems(){
		//setup fixtures

	  int CONFIGURED_NUMBER_OF_ITEMS = 10;

	  DataKey<String> key = DataKey.makeDefault(String.class);

		Fixture<DataSpecification> dataDomain=mock(Fixture.class);

		when(dataDomain.sizeOf(key)).thenReturn(CONFIGURED_NUMBER_OF_ITEMS);

		String[] expected = new String[]{"1","2","3","4","5","6","7","8","9","10"};

		Supplier<String> stringSupplier=mock(Supplier.class);

		when(stringSupplier.get()).thenReturn(expected[0],expected[1],expected[2],expected[3],expected[4],expected[5],expected[6],expected[7],expected[8],expected[9]);

		SupplierGenerationStrategyAdapter<String, DataSpecification> sut=new SupplierGenerationStrategyAdapter<>(key, stringSupplier, new ArrayList<DataKey<?>>());

		//exercise sut
		Iterable<String> actual = sut.generate(dataDomain);

		//verify outcome
		assertThat(actual).containsOnly(expected);

	}
}
