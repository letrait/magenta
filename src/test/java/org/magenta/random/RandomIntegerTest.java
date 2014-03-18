package org.magenta.random;

import java.util.Locale;
import java.util.Random;

import org.junit.Test;
import org.magenta.random.RandomInteger;
import org.mockito.Mockito;

import com.google.common.collect.Range;

import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class RandomIntegerTest {

	public static Range<Integer> SINGLE_POSITIVE = Range.closed(1, 1);
	public static Range<Integer> SINGLE_NEGATIVE = Range.closed(-1, -1);
	public static Range<Integer> LOWER_BOUND_TO_PLUS_ONE = Range.atMost(1);
	public static Range<Integer> MINUS_ONE_TO_UPPER_BOUND = Range.atLeast(-1);

	public static Range<Integer> LOWER_BOUND_TO_ZERO = Range.atMost(0);
	public static Range<Integer> ZERO_TO_UPPER_BOUND = Range.atLeast(0);

	@Test
	public void testAnyWithResolutionOf2() {

		// setup fixtures
		Random random = mock(Random.class);
		RandomInteger sut = new RandomInteger(random).resolution(2);

		// first case
		int oddNumber = 17;

		when(random.nextInt(anyInt())).thenReturn(oddNumber);

		// exercise SUT
		int actual = sut.any();

		// verify outcome
		assertThat(actual % 2).isEqualTo(0);

		// second case
		int evenNumber = 96;

		when(random.nextInt(anyInt())).thenReturn(evenNumber);

		// exercise SUT
		actual = sut.any();

		// verify outcome
		assertThat(actual % 2).isEqualTo(0);

	}

	@Test
	public void testAny() {

		// setup fixtures
		RandomInteger sut = newConstrainedInstance(SINGLE_POSITIVE);
		int expected = SINGLE_POSITIVE.lowerEndpoint();

		// exercise SUT
		int actual = sut.any();

		// verify outcome
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	public void testAnyIntegerPositive() {

		// setup fixtures
		RandomInteger sut = newConstrainedInstance(LOWER_BOUND_TO_ZERO);
		int expected = 0;

		// exercise SUT
		int actual = sut.anyPositive();

		// verify outcome
		assertThat(actual).isGreaterThanOrEqualTo(0)
				.isEqualTo(expected);

	}

	@Test
	public void testAnyIntegerNegative() {

		// setup fixtures
		RandomInteger sut = newConstrainedInstance(ZERO_TO_UPPER_BOUND);
		int expected = 0;

		// exercise SUT
		int actual = sut.anyNegative();

		// verify outcome
		assertThat(actual).isLessThanOrEqualTo(0)
				.isEqualTo(expected);

	}

	@Test
	public void testAnyIntegerPositiveButZero() {

		// setup fixtures
		RandomInteger sut = newConstrainedInstance(LOWER_BOUND_TO_PLUS_ONE);
		int expected = 1;

		// exercise SUT
		int actual = sut.anyPositiveButZero();

		// verify outcome
		assertThat(actual).isGreaterThan(0)
				.isEqualTo(expected);

	}

	@Test
	public void testAnyIntegerNegativeButZero() {

		// setup fixtures
		RandomInteger sut = newConstrainedInstance(MINUS_ONE_TO_UPPER_BOUND);
		int expected = -1;

		// exercise SUT
		int actual = sut.anyNegativeButZero();

		// verify outcome
		assertThat(actual).isLessThan(0)
				.isEqualTo(expected);

	}

	@Test(expected = IllegalStateException.class)
	public void testIllegalState() {
		// setup fixtures
		RandomInteger sut = newConstrainedInstance(Range.greaterThan(0));

		// exercise SUT
		sut.anyNegative();
	}

	private RandomInteger newConstrainedInstance(Range<Integer> constraint) {
		RandomInteger sut = new RandomInteger(new Random(), constraint, 1);
		return sut;
	}
}
