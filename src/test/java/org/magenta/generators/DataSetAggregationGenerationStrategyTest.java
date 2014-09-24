package org.magenta.generators;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.core.GenericDataSet;
import org.magenta.random.RandomBuilder;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class DataSetAggregationGenerationStrategyTest {


	@Test
	public void testGenerate(){

		//setup fixtures

		//mocked random that will simulate a random iteration
		Random random = mock(Random.class);
		when(random.nextInt(Mockito.anyInt())).thenReturn(0,1,0,2,0,0,0,0,0);

		RandomBuilder randomizer = RandomBuilder.PROVIDER.get(random);

		//keys
		DataKey<Integer> integersKey=DataKey.makeDefault(Integer.class);
		DataKey<Double> doublesKey=DataKey.makeDefault(Double.class);
		DataKey<Long> longsKey=DataKey.makeDefault(Long.class);

		DataSet<Integer> integers = new GenericDataSet<>(Arrays.asList(1,2,3,4), Integer.class, randomizer);
		DataSet<Double> doubles = new GenericDataSet<>(Arrays.asList(5d,6d), Double.class, randomizer);
		DataSet<Long> longs = new GenericDataSet<>(Arrays.asList(7L,8L,9L), Long.class, randomizer);

		DataDomain<DataSpecification> domain = mock(DataDomain.class);

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

		RandomBuilder randomizer = RandomBuilder.PROVIDER.get(random);

		//keys
		DataKey<Integer> integersKey=DataKey.makeDefault(Integer.class);
		DataKey<Double> doublesKey=DataKey.makeDefault(Double.class);
		DataKey<Long> longsKey=DataKey.makeDefault(Long.class);

		DataSet<Integer> integers = new GenericDataSet<>(Arrays.asList(1,2,3,4), Integer.class, randomizer);
		DataSet<Double> doubles = new GenericDataSet<>(Arrays.asList(5d,6d), Double.class, randomizer);
		DataSet<Long> longs = new GenericDataSet<>(Arrays.asList(7L,8L,9L), Long.class, randomizer);

		DataDomain<DataSpecification> domain = mock(DataDomain.class);

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
