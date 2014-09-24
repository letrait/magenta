package org.magenta.core;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataDomainManager;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.mockito.Mockito;

import com.google.common.eventbus.EventBus;

public class GeneratedDataSetTest {


	@Test
	public void testConstructor(){

		//setup fixtures
		Integer[] expected={1,2,3,4,5};


		DataDomainManager<DataSpecification> domain=mock(DataDomainManager.class);
		GenerationStrategy<Integer, DataSpecification> strategy=mock(GenerationStrategy.class);

		GeneratedDataSet<Integer,DataSpecification> sut=new GeneratedDataSet<>(domain, strategy, DataKey.makeDefault(Integer.class), mock(EventBus.class));

		//exercise sut

		//verify outcome
		assertThat(sut.isGenerated()).isTrue();


	}

	@Test
	public void testGet(){

		//setup fixtures
		Integer[] expected={1,2,3,4,5};


		DataDomainManager<DataSpecification> domain=mock(DataDomainManager.class);
		GenerationStrategy<Integer, DataSpecification> strategy=mock(GenerationStrategy.class);

		when(strategy.generate(Mockito.any(DataDomain.class))).thenReturn(Arrays.asList(expected));

		GeneratedDataSet<Integer,DataSpecification> sut=new GeneratedDataSet<>(domain, strategy, DataKey.makeDefault(Integer.class), mock(EventBus.class));

		//exercise sut
		Iterable<Integer> actual=sut.get();
		sut.get();
		sut.get();

		//verify outcome
		assertThat(sut.isEmpty()).isFalse();
		assertThat(actual).containsExactly(expected);
		verify(strategy,Mockito.times(1)).generate(domain);

	}
}
