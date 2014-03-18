package org.magenta;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.QualifiedDataSet;
import org.magenta.random.Randoms;
import org.mockito.Mockito;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

public class DataSetKeyTest {

	@Test
	public void testMake() {

		// setup fixture
		String expectedQualifier = "any qualifier";
		Class<String> expectedType = String.class;

		// exercise SUT
		DataKey<String> actual = DataKey.makeQualified(expectedQualifier, expectedType);

		// verify outcome
		assertThat(actual.getType(), equalTo(expectedType));
		assertThat(actual.getQualifier(), equalTo(expectedQualifier));
		
		// misc assertion
		assertThat(actual,equalTo(actual));
		assertThat(actual,not(equalTo(new Object())));
		assertThat(actual,not(equalTo(null)));
		assertThat(actual.toString()).contains(expectedQualifier);
		assertThat(actual.toString()).contains(expectedType.getName());

	}

	@Test
	public void testMakeDefault() {

		// setup fixture
		Class<String> expectedType = String.class;

		// exercise SUT
		DataKey<String> actual = DataKey.makeDefault(expectedType);

		// verify outcome
		assertThat(actual.getType()).isEqualTo(expectedType);
		assertThat(actual.getQualifier()).isEqualTo(DataKey.DEFAULT_QUALIFIER);
		assertThat(actual.isDefault()).isTrue();

	}
	
	@Test
	public void testMakeEmtpyDataSet(){
		
			// setup fixture
			Class<String> type = String.class;
			DataKey<String> sut = DataKey.makeDefault(type);

			// exercise SUT
			DataSet<String> actual =  sut.asEmptyDataSet();
			
			// verify outcome
			assertThat(actual.isEmpty()).isTrue();
	}

	@Test
	public void testEquals_same_type() {

		// setup fixture
		Class<String> type = String.class;
		DataKey<String> ref1 = DataKey.makeDefault(type);
		DataKey<String> ref2 = DataKey.makeDefault(type);

		// exercise SUT and verify outcome
		assertThat(ref1, equalTo(ref2));
		assertThat(ref1.hashCode(), equalTo(ref2.hashCode()));

	}

	@Test
	public void testEquals_same_type_same_qualifier() {

		// setup fixture
		Class<String> type = String.class;
		Class<String> sameType = String.class;
		String qualifier = "qualifier";
		String sameQualifier = "qualifier";
		DataKey<String> ref1 = DataKey.makeQualified(qualifier, type);
		DataKey<String> ref2 = DataKey.makeQualified(sameQualifier, sameType);

		// exercise SUT and verify outcome
		assertThat(ref1, equalTo(ref2));

	}

	@Test
	public void testEquals_same_type_different_qualifier() {

		// setup fixture
		Class<String> type = String.class;
		Class<String> sameType = String.class;
		String qualifier = "qualifier";
		String differentQualifier = "different qualifier";
		DataKey<String> ref1 = DataKey.makeQualified(qualifier, type);
		DataKey<String> ref2 = DataKey.makeQualified(differentQualifier, sameType);

		// exercise SUT and verify outcome
		assertThat(ref1, not(equalTo(ref2)));

	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testEquals_different_type_same_qualifier() {

		// setup fixture
		Class<String> type = String.class;
		Class<Integer> differentType = Integer.class;
		String qualifier = "qualifier";
		String sameQualifier = "qualifier";
		DataKey ref1 = DataKey.makeQualified(qualifier, type);
		DataKey ref2 = DataKey.makeQualified(sameQualifier, differentType);

		// exercise SUT and verify outcome
		assertThat(ref1, not(equalTo(ref2)));

	}
	
	@Test
	public void testAsDataSet_static(){
		//setup fixtures
		
		DataKey<String> sut = DataKey.makeDefault(String.class);
		
		//exercise sut
		QualifiedDataSet<String> actual=sut.asDataSet(Randoms.singleton(),"foo","bar");
		
		//verify outcome
		assertThat(actual.getKey()).isSameAs(sut);
		assertThat(actual.list()).contains("foo","bar").hasSize(2);
	}
	
	@Test
	public void testAsDataSet_DataDomain(){
			
			//setup fixtures
			DataKey<String> sut = DataKey.makeDefault(String.class);
			DataDomain<DataSpecification> domain=mock(DataDomain.class);
			DataSet<String> expected=mock(DataSet.class);
			
			when(domain.dataset(Mockito.any(DataKey.class))).thenReturn(expected);
			
			//exercise sut
			DataSet<String> actual=sut.getDataSetFrom(domain);
			
			//verify outcome
			assertThat(actual).isEqualTo(expected);
			verify(domain).dataset(sut);
			
	}
	

	
	

}
