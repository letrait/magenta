package org.magenta.generators;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.ImplicitGenerationStrategy;
import org.magenta.SimpleGenerationStrategy;
import org.magenta.annotations.TriggeredGeneration;
import org.magenta.annotations.Key;
import org.magenta.generators.GeneratorAnnotationHelper;

import static org.fest.assertions.api.Assertions.assertThat;

public class GeneratorAnnotationHelperTest {

	@Test
	public void testGetAffectedDataSetWithNoKey(){
		
		//setup fixtures

		//exercise sut
		List<DataKey<?>> actual = GeneratorAnnotationHelper.getAffectedDataSet(WithNoKey.class);
		
		//verify outcome
		assertThat(actual).isEmpty();
	}
	
	@Test
	public void testGetAffectedDataSetWithOneKey(){
		
		//setup fixtures
		
		DataKey<?>[] expected=new DataKey<?>[]{DataKey.makeDefault(Date.class)};
		
		//exercise sut
		List<DataKey<?>> actual = GeneratorAnnotationHelper.getAffectedDataSet(WithOneKey.class);
		
		//verify outcome
		assertThat(actual).containsExactly(expected);
	}
	
	@Test
	public void testGetAffectedDataSetWithTwoKeys(){
		
		//setup fixtures
		
		DataKey<?>[] expected=new DataKey<?>[]{DataKey.makeDefault(Date.class),DataKey.makeDefault(Integer.class)};
		
		//exercise sut
		List<DataKey<?>> actual = GeneratorAnnotationHelper.getAffectedDataSet(WithTwoKeys.class);
		
		//verify outcome
		assertThat(actual).containsExactly(expected);
	}
	
	@Test
	public void testGetAffectedDataSetWithQualifiedTwoKeys(){
		
		//setup fixtures
		
		DataKey<?>[] expected=new DataKey<?>[]{DataKey.makeQualified("QUALIFIED_DATE",Date.class),DataKey.makeQualified("QUALIFIED_INTEGER", Integer.class)};
		
		//exercise sut
		List<DataKey<?>> actual = GeneratorAnnotationHelper.getAffectedDataSet(WithTwoQualifiedKeys.class);
		
		//verify outcome
		assertThat(actual).containsExactly(expected);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testGetAffectedDataSetWithEmptyKey(){
		
		//setup fixtures

		//exercise sut
		List<DataKey<?>> actual = GeneratorAnnotationHelper.getAffectedDataSet(WithEmptyKey.class);
		
		
	}
	

	public static class WithNoKey extends SimpleGenerationStrategyDummyAdapter{
		
	}
	
	
	@TriggeredGeneration({})
	public static class WithEmptyKey extends SimpleGenerationStrategyDummyAdapter{
		
	}
	
	@TriggeredGeneration(@Key(Date.class))
	public static class WithOneKey extends ImplicitGenerationStrategyDummyAdapter{
		
	}
	
	@TriggeredGeneration({@Key(Date.class),@Key(Integer.class)})
	public static class WithTwoKeys extends ImplicitGenerationStrategyDummyAdapter{
		
	}
	
	@TriggeredGeneration({@Key(value = Date.class,qualifier = "QUALIFIED_DATE"),@Key(value = Integer.class, qualifier = "QUALIFIED_INTEGER")})
	public static class WithTwoQualifiedKeys extends SimpleGenerationStrategyDummyAdapter{
		
	}
	
	public static class SimpleGenerationStrategyDummyAdapter implements SimpleGenerationStrategy<String,DataSpecification>{

		@Override
		public String generateItem(DataDomain<? extends DataSpecification> dataDomain) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getPreferredNumberOfItems(DataSpecification specification) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	public static class ImplicitGenerationStrategyDummyAdapter implements ImplicitGenerationStrategy<String,DataSpecification>{

		@Override
		public Iterable<String> generate(DataDomain<? extends DataSpecification> datasetMap) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
}
