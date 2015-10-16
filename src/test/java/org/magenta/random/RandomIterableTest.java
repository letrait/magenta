package org.magenta.random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class RandomIterableTest {

  @Mock
  private Random random;

  private Random previous;

  @Before
  public void configureFluentRandom(){
    this.previous = FluentRandom.getRandom();
    FluentRandom.setRandom(random);
  }

  @After
  public void configureFluentRandomBack(){
    FluentRandom.setRandom(previous);
  }

	@Test
	public void testTraversal_mocked_case_1(){

		//setup fixtures

		List<Integer> list1=Arrays.asList(1,2,3);
		List<Integer> list2=Arrays.asList(4,5,6);
		List<Integer> list3=Arrays.asList(7,8,9);

		Integer[] expected = new Integer[]{1,4,7,2,5,8,3,6,9};

		//sequence of random to get the expected result
		when(random.nextInt(Mockito.anyInt())).thenReturn(0,1,2,0,1,2,0,0,0);

		List<List<Integer>> integerLists=Lists.newArrayList(list1,list2,list3);

		MixedIterable<Integer> sut=new MixedIterable<>(integerLists);

		//exercise sut
		List<Integer> actual=Lists.newArrayList(sut);

		//verify outcome
		assertThat(actual).containsExactly(expected);
	}

	@Test
	public void testTraversal_mocked_case_2(){

		//setup fixtures
		Random random=mock(Random.class);
	  FluentRandom.setRandom(random);


		List<Integer> list1=Arrays.asList(1,2,3);
		List<Integer> list2=Arrays.asList(4,5,6);
		List<Integer> list3=Arrays.asList(7,8,9);

		Integer[] expected = new Integer[]{7,1,8,9,4,2,3,5,6};

	//sequence of random to get the expected result
		when(random.nextInt(Mockito.anyInt())).thenReturn(2,0,2,2,1,0,0,0,0);

		List<List<Integer>> integerLists=Lists.newArrayList(list1,list2,list3);

		MixedIterable<Integer> sut=new MixedIterable<>(integerLists);

		//exercise sut
		List<Integer> actual=Lists.newArrayList(sut);

		//verify outcome
		assertThat(actual).containsExactly(expected);
	}

	@Test
	public void testTraversalWhenOneOfTheListIsEmpty(){

	  //setup fixtures

    List<Integer> list1=Arrays.asList(1,2,3);
    List<Integer> list2=Arrays.asList(4,5,6);
    List<Integer> list3=Arrays.asList();

    Integer[] expected = new Integer[]{1,2,3,4,5,6};

    List<List<Integer>> integerLists=Lists.newArrayList(list1,list2,list3);

    MixedIterable<Integer> sut=new MixedIterable<>(integerLists);

    //exercise sut (should not throw error)
    List<Integer> actual=Lists.newArrayList(sut);

    // verify outcome
    assertThat(actual).containsOnly(expected);
	}

	 @Test
	  public void testTraversalWhenAllListAreEmpty(){

	    //setup fixtures
	    List<Integer> list1=Arrays.asList();
	    List<Integer> list2=Arrays.asList();
	    List<Integer> list3=Arrays.asList();

	    Integer[] expected = new Integer[]{};

	    List<List<Integer>> integerLists=Lists.newArrayList(list1,list2,list3);

	    MixedIterable<Integer> sut=new MixedIterable<>(integerLists);

	    //exercise sut (should not throw error)
	    List<Integer> actual=Lists.newArrayList(sut);

	    // verify outcome
	    assertThat(actual).isEmpty();
	  }

	//TODO test with a list that is a view and see when it is actually rendered
}
