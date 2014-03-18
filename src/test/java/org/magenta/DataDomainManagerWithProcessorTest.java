package org.magenta;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;
import org.magenta.random.Randoms;

public class DataDomainManagerWithProcessorTest {

	@Test
	@Ignore
	public void testProcessor(){
		//setup fixtures
		AtomicInteger int1=new AtomicInteger(1);
		AtomicInteger int2=new AtomicInteger(2);


		Processor<DataSpecification> processor=new IntegerIncrementerProcessor();

		DataDomainManager<SimpleDataSpecification> testDataDomain=DataDomainManager.newRoot("test", SimpleDataSpecification.create(), Randoms.singleton());
		testDataDomain.newDataSet(AtomicInteger.class).composedOf(int1,int2);

		//exercise sut
		testDataDomain.newProcessor(processor);

		//verify outcome
		assertThat(int1.get()).isEqualTo(1);
		assertThat(int2.get()).isEqualTo(2);

		AtomicInteger[] integers=testDataDomain.newNode("child").dataset(AtomicInteger.class).array();

		assertThat(integers[0].get()).isEqualTo(2);
		assertThat(integers[1].get()).isEqualTo(3);

		integers=testDataDomain.newNode("child").dataset(AtomicInteger.class).array();

		assertThat(integers[0].get()).isEqualTo(2);
		assertThat(integers[1].get()).isEqualTo(3);
	}

	public static class IntegerIncrementerProcessor implements Processor<DataSpecification>{

		List keys=Arrays.asList((DataKey<?>)DataKey.makeDefault(AtomicInteger.class));

		@Override
		public void process(DataDomain<? super DataSpecification> dataDomain) {

			for(AtomicInteger integer:dataDomain.dataset(AtomicInteger.class).array()){
				integer.incrementAndGet();
			}

		}

		@Override
		public List<DataKey<?>> getAffectedDataSetKeys() {

			return keys;
		}

	}
}
