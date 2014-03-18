package org.magenta.generators;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.ImplicitGenerationStrategy;
import org.magenta.generators.ImplicitGenerationStrategyAdapter;

import com.google.common.collect.Lists;

public class ImplicitGenerationStrategyAdatperTest {

	@Test
	public void testGenerate() {
		// setup fixtures
		DataDomain<DataSpecification> domain = mock(DataDomain.class);
		List<DataKey<?>> affectedDataSet = Lists.newArrayList();
		ImplicitGenerationStrategy<Integer, DataSpecification> candidate = mock(ImplicitGenerationStrategy.class);

		ImplicitGenerationStrategyAdapter<Integer, DataSpecification> sut = new ImplicitGenerationStrategyAdapter<>(candidate,affectedDataSet);

		Integer[] expected = new Integer[] { 1, 2, 3, 4, 5, 6 };

		when(candidate.generate(domain)).thenReturn(Arrays.asList(expected));

		// exercise sut
		Iterable<Integer> actual = sut.generate(domain);

		// verify outcome
		assertThat(actual).containsExactly(expected);

	}

	@Test
	public void testGenerateSpecificNumberOfItems() {
		// setup fixtures
		DataDomain<DataSpecification> domain = mock(DataDomain.class);
		List<DataKey<?>> affectedDataSet = Lists.newArrayList();
		ImplicitGenerationStrategy<Integer, DataSpecification> candidate = mock(ImplicitGenerationStrategy.class);

		ImplicitGenerationStrategyAdapter<Integer, DataSpecification> sut = new ImplicitGenerationStrategyAdapter<>(candidate, affectedDataSet);

		when(candidate.generate(domain)).thenReturn(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

		Integer[] expected = new Integer[] { 1, 2, 3 };

		// exercise sut
		Iterable<Integer> actual = sut.generate(3, domain);

		// verify outcome
		assertThat(actual).containsExactly(expected);

	}
}
