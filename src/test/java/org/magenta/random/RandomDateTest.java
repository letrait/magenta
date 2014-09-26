package org.magenta.random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Ticker;
import com.google.common.collect.Range;

@RunWith(MockitoJUnitRunner.class)
public class RandomDateTest {

	@Mock
	private Random random;

	@Mock
	private Ticker ticker;

	@Test
	public void testAny() {
		// setup fixtures
		RandomDate sut = createSimpleRandomDate();
		Long oneHundredYears = 1000L * 60L * 60L * 24L * 365L * 100L;
		Date oneHundredYearsAfter1969 = new Date(oneHundredYears);
		when(random.nextLong()).thenReturn(oneHundredYears);

		// exercise SUT
		Date actual = sut.any();

		// verify outcome

		assertThat(actual).isEqualTo(oneHundredYearsAfter1969);
	}

	private RandomDate createSimpleRandomDate() {
		RandomDate sut = new RandomDate(1L, Range.closedOpen(new Date(0), new Date(System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 365L * 100L))),
				new RandomLong(random), ticker);
		return sut;
	}

	@Test
	public void testAnyInFuture() {
		// setup fixtures
		RandomDate sut = createSimpleRandomDate();
		Long twoHours = 1000L * 60L * 60L * 2;
		Long current = System.currentTimeMillis();
		Date expected = new Date(current + twoHours);
		when(random.nextLong()).thenReturn(twoHours);
		when(ticker.read()).thenReturn(TimeUnit.MILLISECONDS.toNanos(current));

		// exercise SUT
		Date actual = sut.anyInTheFuture(1, TimeUnit.DAYS);

		// verify outcome

		assertThat(actual).isEqualTo(expected);

	}

	@Test
	public void testAnyInPast() {
		// setup fixtures
		RandomDate sut = createSimpleRandomDate();
		Long twoHours = 1000L * 60L * 60L * 2;
		Long aDay = 1000L * 60L * 60L * 24L;
		Long current = System.currentTimeMillis();
		Date expected = new Date(current - aDay + twoHours);
		when(random.nextLong()).thenReturn(twoHours);
		when(ticker.read()).thenReturn(TimeUnit.MILLISECONDS.toNanos(current));

		// exercise SUT
		Date actual = sut.anyInThePast(1, TimeUnit.DAYS);

		// verify outcome
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testAnyInTheLast() {
		// setup fixtures
		RandomDate sut = createSimpleRandomDate();
		Long current = System.currentTimeMillis();
		Long twoHours = 1000L * 60L * 60L * 2;
		Long aDay = 1000L * 60L * 60L * 24L;
		Date expected = new Date(current - aDay + twoHours);
		when(random.nextLong()).thenReturn(twoHours);
		when(ticker.read()).thenReturn(TimeUnit.MILLISECONDS.toNanos(current));

		// exercise SUT
		Date actual = sut.anyInTheLast(new Date(current), 1, TimeUnit.DAYS);

		// verify outcome
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testAnyInTheNext() {
		// setup fixtures
		RandomDate sut = createSimpleRandomDate();
		Long current = System.currentTimeMillis();
		Long twoHours = 1000L * 60L * 60L * 2;
		Date expected = new Date(current + twoHours);
		when(random.nextLong()).thenReturn(twoHours);
		when(ticker.read()).thenReturn(TimeUnit.MILLISECONDS.toNanos(current));

		// exercise SUT
		Date actual = sut.anyInTheNext(new Date(current), 1, TimeUnit.DAYS);

		// verify outcome
		assertThat(actual).isEqualTo(expected);
	}
}
