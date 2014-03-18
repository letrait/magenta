package org.magenta.generators;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.generators.TransformedStrategy;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class TransformedStrategyTest {

	@Test
	public void testGenerate() {
		// setup fixtures

		DataDomain<DataSpecification> dataDomain = mock(DataDomain.class);
		GenerationStrategy<Integer, DataSpecification> strategy = createIntegerGenerationStrategy(1, 2, 3, 4, 5, 6);

		Function<Integer, String> converter = integerToStringFunction();
		Predicate<Integer> filter = oddNumberFilter();
		TransformedStrategy<String, Integer, DataSpecification> sut = new TransformedStrategy<>(strategy, filter, converter);

		String[] expected = new String[] { "1", "3", "5" };

		// exercise sut
		Iterable<String> actual = sut.generate(dataDomain);

		// verify outcome
		assertThat(actual).containsExactly(expected);
	}

	@Test
	public void testGenerateSpecificNumberOfElements() {
		// setup fixtures

		DataDomain<DataSpecification> dataDomain = mock(DataDomain.class);
		GenerationStrategy<Integer, DataSpecification> strategy = createIntegerGenerationStrategy(1, 2, 3, 4, 5, 6);

		Function<Integer, String> converter = integerToStringFunction();
		Predicate<Integer> filter = oddNumberFilter();
		TransformedStrategy<String, Integer, DataSpecification> sut = new TransformedStrategy<>(strategy, filter, converter);

		String[] expected = new String[] { "1", "3" };

		// exercise sut
		Iterable<String> actual = sut.generate(2, dataDomain);

		// verify outcome
		assertThat(actual).containsExactly(expected);
	}

	private GenerationStrategy<Integer, DataSpecification> createIntegerGenerationStrategy(final Integer... values) {

		GenerationStrategy<Integer, DataSpecification> g = mock(GenerationStrategy.class);

		when(g.generate(Mockito.any(DataDomain.class))).thenReturn(Arrays.asList(values));

		when(g.generate(Mockito.anyInt(), Mockito.any(DataDomain.class))).thenAnswer(new Answer<Iterable<Integer>>() {

			@Override
			public Iterable<Integer> answer(InvocationOnMock invocation) throws Throwable {
				Integer numberOfElements = (Integer) invocation.getArguments()[0];

				return Arrays.asList(values)
						.subList(0, numberOfElements);
			}

		});

		return g;
	}

	private Predicate<Integer> oddNumberFilter() {
		return new Predicate<Integer>() {
			@Override
			public boolean apply(Integer input) {
				return Math.abs(input) % 2 == 1;
			}
		};
	}

	private Function<Integer, String> integerToStringFunction() {
		return new Function<Integer, String>() {
			@Override
      public String apply(Integer input) {
				return String.valueOf(input);
			};
		};
	}
}
