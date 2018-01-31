package org.magenta.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.GenerationStrategy;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TransformedStrategyTest {

	@Test
	public void testGenerate() {
		// setup fixtures

		Fixture<DataSpecification> dataDomain = mock(Fixture.class);
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

		Fixture<DataSpecification> dataDomain = mock(Fixture.class);
		GenerationStrategy<Integer, DataSpecification> strategy = createIntegerGenerationStrategy(1, 2, 3, 4, 5, 6);

		Function<Integer, String> converter = integerToStringFunction();
		Predicate<Integer> filter = oddNumberFilter();
		TransformedStrategy<String, Integer, DataSpecification> sut = new TransformedStrategy<>(strategy, filter, converter);

		String[] expected = new String[] { "1", "3" };

		// exercise sut
		Iterable<String> actual = sut.generate(3, dataDomain);

		// verify outcome
		assertThat(actual).containsExactly(expected);
	}

	private GenerationStrategy<Integer, DataSpecification> createIntegerGenerationStrategy(final Integer... values) {

		GenerationStrategy<Integer, DataSpecification> g = mock(GenerationStrategy.class);

		when(g.generate(Mockito.any(Fixture.class))).thenReturn(Arrays.asList(values));

		when(g.generate(Mockito.anyInt(), Mockito.any(Fixture.class))).thenAnswer(new Answer<Iterable<Integer>>() {

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
			public boolean apply(Integer input) {
				return Math.abs(input) % 2 == 1;
			}

      @Override
      public boolean test(Integer input) {
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
