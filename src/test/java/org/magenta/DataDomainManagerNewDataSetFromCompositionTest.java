package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.magenta.testing.MagentaAssertions.assertThat;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;
import org.magenta.core.GenericDataSet;
import org.magenta.random.FluentRandom;



public class DataDomainManagerNewDataSetFromCompositionTest extends FixtureFactoryTestSupport{

	@Test
	public void testConstructorAndGetter(){
		//setup fixtures
	  SimpleDataSpecification specification = SimpleDataSpecification.create();
		FluentRandom randomizer = FluentRandom.get(new Random());
		String expectedName = "DataDomainSingleNodeTest";

		//exercise sut
		Fixture<SimpleDataSpecification> domain = FixtureFactory.newRoot(expectedName, specification, randomizer);

		//verify outcome
		assertThat(domain).hasName(expectedName)
				.hasSpecification(specification)
				.hasRandomizer(randomizer);


		//the rest should be null or empty
		assertThat(domain.getParent()).isNull();
		assertThat(domain.datasets()).isEmpty();
		assertThat(domain.datasetKeys()).isEmpty();
		assertThat(domain.strategies()).isEmpty();
		assertThat(domain.strategyKeys()).isEmpty();
	}

	/**
	 * A {@link Fixture} must throw a {@link DataSetNotFoundException} if the requested dataset is not found.
	 */
	@Test(expected = DataSetNotFoundException.class)
	public void testThrowNotFoundException(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();
		DataSet<String> colors = domain.newDataSet(String.class).composedOf("red","blue","green");

		//exercise sut
		//should throw exception has there are no dataset of type integer
		domain.dataset(Integer.class);

	}

	@Test
	public void testNewDataSet(){
			//setup fixtures
			FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

			DataKey<String> colorKey = DataKey.makeDefault(String.class);

			//exercise sut
			DataSet<String> colors = domain.newDataSet(String.class).composedOf("red","blue","green");
			DataSet<String> sameColors = domain.dataset(String.class);
			DataSet<String> stillSameColors = domain.dataset(colorKey);

			Set<DataKey<?>> actualKeys = domain.datasetKeys();
			Iterable<DataSet<?>> actualDataSets = domain.datasets();


			//verify outcome
			assertThat(colors).isNotNull().isEqualTo(sameColors).isEqualTo(stillSameColors);
			assertThat(colors.list()).containsExactly("red","blue","green");
			assertThat(actualKeys).containsOnly(colorKey);
			assertThat(actualDataSets).containsOnly(colors);
	}

	@Test
	public void testNewDataSet_ComposedOf_varargs(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

		//exercise sut
		DataSet<String> colors = domain.newDataSet(String.class).composedOf("red","blue","green");
		DataSet<Integer> primes = domain.newDataSet(Integer.class).composedOf(2,3,5,7,11);


		//verify outcome
		assertThat(domain).theDataSet(String.class)
				.isNotNull()
				.containsExactly("red", "blue", "green")
				.isEqualTo(colors);

	  assertThat(domain)
				.theDataSet(Integer.class)
				.isNotNull()
				.containsExactly(2, 3, 5, 7, 11)
				.isEqualTo(primes);

	}

	@Test
	public void testNewDataSet_ComposedOf_iterable(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

		//exercise sut
		DataSet<String> colors = domain.newDataSet(String.class).composedOf(Arrays.asList("red","blue","green"));
		DataSet<Integer> primes = domain.newDataSet(Integer.class).composedOf(Arrays.asList(2,3,5,7,11));


		//verify outcome
	//verify outcome
			assertThat(domain).theDataSet(String.class)
					.isNotNull()
					.containsExactly("red", "blue", "green")
					.isEqualTo(colors);

		  assertThat(domain)
					.theDataSet(Integer.class)
					.isNotNull()
					.containsExactly(2, 3, 5, 7, 11)
					.isEqualTo(primes);

	}

	@Test
	public void testNewDataSet_ComposedOf_dataset(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

		DataSet<String> expectedColors = new GenericDataSet<>(Arrays.asList("red","blue","green"), String.class, domain.getRandomizer());

		//exercise sut
		DataSet<String> colors = domain.newDataSet(String.class).composedOf(expectedColors);

		//verify outcome
		assertThat(domain).theDataSet(String.class)
					.isNotNull()
					.isEqualTo(expectedColors)
					.isEqualTo(colors);


	}

	@Test
	public void testNewDataSet_ComposedOf_override(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

		//exercise sut
		DataSet<String> colors = domain.newDataSet(String.class).composedOf("red","blue","green");
		DataSet<String> overridedColors = domain.newDataSet(String.class).composedOf("cyan","yellow","magenta");

		//verify outcome

		assertThat(domain).theDataSet(String.class)
		.isNotNull()
		.isNotEqualTo(colors)
		.containsExactly("cyan","yellow","magenta")
		.isEqualTo(overridedColors);


	}

	@Test
	public void testNewDataSet_ComposedOf_transform(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

		//exercise sut
		DataSet<String> numberInString = domain.newDataSet(String.class)
				.transformed(i->i.toString())
				.composedOf(1, 2, 3);

		//verify outcome

		assertThat(domain).theDataSet(String.class)
		.isNotNull()
		.isEqualTo(numberInString)
		.containsExactly("1","2","3");

	}

	@Test
	public void testNewDataSet_ComposedOf_filter(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

		Predicate<Integer> isPositive = new Predicate<Integer>(){

			public boolean apply(Integer input) {
				return input.intValue()>=0;
			}

      @Override
      public boolean test(Integer input) {
        return input.intValue()>=0;
      }
		};

		//exercise sut
		DataSet<Integer> positiveNumbers = domain.newDataSet(Integer.class)
				.filtered(isPositive)
				.composedOf(-3,-2,-1,0,1, 2, 3);

		//verify outcome
		assertThat(domain).theDataSet(Integer.class)
		.isNotNull()
		.isEqualTo(positiveNumbers)
		.containsExactly(0,1, 2, 3);

	}

	@Test
	public void testNewDataSet_ComposedOf_filter_and_transform(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

		Predicate<Integer> isPositive = new Predicate<Integer>(){

			public boolean apply(Integer input) {
				return input.intValue()>=0;
			}

      @Override
      public boolean test(Integer input) {
        return input.intValue()>=0;
      }
		};

		//shh shh
		Function<Integer,String> integerToString=i->i.toString();

		//exercise sut
		DataSet<String> positiveNumbers = domain.newDataSet(String.class)
				.transformed(integerToString)
				.filtered(isPositive)
				.composedOf(-3,-2,-1,0,1, 2, 3);

		//verify outcome
		assertThat(domain).theDataSet(String.class)
		.isNotNull()
		.isEqualTo(positiveNumbers)
		.containsExactly("0","1", "2", "3");

	}

	@Test
	public void testNewDataSet_ComposedOf_transform_chain(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();

		Function<Integer,Integer> multiplyByTwo = new Function<Integer,Integer>(){

			@Override
			public Integer apply(Integer input) {
				return input.intValue()*2;
			}

		};

		//exercise sut
		DataSet<String> doubleNumbers = domain.newDataSet(String.class)
				.transformed(i->i.toString())
				.transformed(multiplyByTwo)
				.composedOf(1, 2, 3);

		//verify outcome

		assertThat(domain).theDataSet(String.class)
		.isNotNull()
		.isEqualTo(doubleNumbers)
		.containsExactly("2","4","6");

	}

	@Test
	public void testRemove(){
		//setup fixtures
		FixtureFactory<SimpleDataSpecification> domain = createAnonymousFixtureFactory();
		DataSet<String> colors = domain.newDataSet(String.class).composedOf("red","blue","green");

		//exercise sut
		DataSet<String> actual = domain.remove(String.class);

		//verify outcome
		assertThat(domain).doesNotContainDatasetFor(String.class);
		assertThat(actual).isEqualTo(colors);

	}


}
