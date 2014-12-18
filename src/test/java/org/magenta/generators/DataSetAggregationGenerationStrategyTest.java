package org.magenta.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.core.GenericDataSet;
import org.magenta.core.PickStrategy;
import org.magenta.core.RandomPickStrategy;
import org.magenta.random.FluentRandom;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class DataSetAggregationGenerationStrategyTest {


	@Test
	public void testGenerate(){

		//setup fixtures

		//mocked random that will simulate a random iteration
		Random random = mock(Random.class);
		when(random.nextInt(Mockito.anyInt())).thenReturn(0,1,0,2,0,0,0,0,0);

		FluentRandom randomizer = FluentRandom.get(random);
		PickStrategy picker = RandomPickStrategy.supplier(randomizer).get();

		//keys
		DataKey<Integer> integersKey=DataKey.makeDefault(Integer.class);
		DataKey<Double> doublesKey=DataKey.makeDefault(Double.class);
		DataKey<Long> longsKey=DataKey.makeDefault(Long.class);

		DataSet<Integer> integers = new GenericDataSet<>(Arrays.asList(1,2,3,4), Integer.class, picker, randomizer);
		DataSet<Double> doubles = new GenericDataSet<>(Arrays.asList(5d,6d), Double.class, picker, randomizer);
		DataSet<Long> longs = new GenericDataSet<>(Arrays.asList(7L,8L,9L), Long.class, picker, randomizer);

		Fixture<DataSpecification> domain = mock(Fixture.class);

		//mapping keys and values
		when(domain.getRandomizer()).thenReturn(randomizer);
		when(domain.dataset(integersKey)).thenReturn(integers);
		when(domain.dataset(doublesKey)).thenReturn(doubles);
		when(domain.dataset(longsKey)).thenReturn(longs);

		DataSetAggregationStrategy<Number, DataSpecification> sut=new DataSetAggregationStrategy<Number, DataSpecification>(integersKey, doublesKey, longsKey);

		//exercise sut
		Iterable<Number> actual = Lists.newArrayList(sut.generate(domain));

		//verify outcome
		assertThat(actual).containsExactly(1,5d,2,7L,3,4,6d,8L,9L);

	}

	@Test
	public void testGenerateSpecificNumberOfItems(){

		//setup fixtures

		//mocked random that will simulate a random iteration
		Random random = mock(Random.class);
		when(random.nextInt(Mockito.anyInt())).thenReturn(0,1,0,2,0,0,0,0,0);

		FluentRandom randomizer = FluentRandom.get(random);
	  PickStrategy picker = RandomPickStrategy.supplier(randomizer).get();

		//keys
		DataKey<Integer> integersKey=DataKey.makeDefault(Integer.class);
		DataKey<Double> doublesKey=DataKey.makeDefault(Double.class);
		DataKey<Long> longsKey=DataKey.makeDefault(Long.class);

		DataSet<Integer> integers = new GenericDataSet<>(Arrays.asList(1,2,3,4), Integer.class, picker, randomizer);
		DataSet<Double> doubles = new GenericDataSet<>(Arrays.asList(5d,6d), Double.class, picker, randomizer);
		DataSet<Long> longs = new GenericDataSet<>(Arrays.asList(7L,8L,9L), Long.class, picker, randomizer);

		Fixture<DataSpecification> domain = mock(Fixture.class);

		//mapping keys and values
		when(domain.getRandomizer()).thenReturn(randomizer);
		when(domain.dataset(integersKey)).thenReturn(integers);
		when(domain.dataset(doublesKey)).thenReturn(doubles);
		when(domain.dataset(longsKey)).thenReturn(longs);

		DataSetAggregationStrategy<Number, DataSpecification> sut=new DataSetAggregationStrategy<Number, DataSpecification>(integersKey, doublesKey, longsKey);

		//exercise sut
		Iterable<Number> actual = Lists.newArrayList(sut.generate(5,domain));

		//verify outcome
		assertThat(actual).containsExactly(1,5d,2,7L,3);

	}
}
