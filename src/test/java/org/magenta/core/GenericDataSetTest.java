package org.magenta.core;

import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.or;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.magenta.DataSet;
import org.magenta.random.RandomBuilder;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Suppliers;

public class GenericDataSetTest {


	@Test
	public void testConstructor_with_iterable(){

		//setup fixtures

		List<Integer> expectedData=Arrays.asList(1,3,5);
		Class<Integer> expectedType=Integer.class;
		RandomBuilder expectedRandomizer=RandomBuilder.PROVIDER.singleton();
		GenericDataSet<Integer> sut=new GenericDataSet<>(Arrays.asList(1,3,5), expectedType, expectedRandomizer);

		//exercise sut
		sut.toString();
		sut.hashCode();

		//verify outcome
		assertThat(sut.isGenerated()).overridingErrorMessage("Expecting the <%s> to be not generated as it should always be the case", sut.toString()).isFalse();
		assertThat(sut.list()).isEqualTo(expectedData);
		assertThat(sut.getType()).isEqualTo(expectedType);
		assertThat(sut.getRandomizer()).isEqualTo(expectedRandomizer);

	}

	@Test
	public void testEmpty() {

		// setup fixtures
		GenericDataSet<String> sut = new GenericDataSet<>(Collections.EMPTY_LIST, String.class,RandomBuilder.PROVIDER.singleton());

		// exercise sut
		boolean actual = sut.isEmpty();

		// verify outcome
		assertThat(actual).isTrue();

	}

	@Test
	public void testEmpty_after_filtering() {

		// setup fixtures
		GenericDataSet<String> sut = Fixtures.createDataSetOf("1","2","3");

		// exercise sut
		boolean actual = sut.filter(containsPattern("5")).isEmpty();

		// verify outcome
		assertThat(actual).isTrue();

	}

  @Test
  public void testList() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";
    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);

    // exercise sut
    List<String> actual = sut.list();

    // verify outcome
    assertThat(actual).containsSequence(element1, element2, element3);
  }

  @Test
  public void testList_with_size() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";
    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);
    int expectedSize = 2;

    // exercise sut
    List<String> actual = sut.list(expectedSize);

    // verify outcome
    assertThat(actual).containsExactly(element1, element2);
  }

  @Test
  public void testRandomList() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";
    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);

    // exercise sut
    List<String> actual = sut.randomList();

    // verify outcome
    assertThat(actual).contains(element1, element2, element3);

  }

  @Test
  public void testRandomList_with_size() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";

    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);
    int expectedSize = 2;

    // exercise sut
    List<String> actual = sut.randomList(expectedSize);

    // verify outcome
    assertThat(actual).hasSize(expectedSize);

  }

  @Test
  public void testArray() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";
    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);

    // exercise sut
    String[] actual = sut.array();

    // verify outcome
    assertThat(actual).containsSequence(element1, element2, element3);
  }

  @Test
  public void testArray_with_size() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";
    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);
    int expectedSize = 2;

    // exercise sut
    String[] actual = sut.array(expectedSize);

    // verify outcome
    assertThat(actual).containsExactly(element1, element2);
  }

  @Test
  public void testRandomArray() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";
    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);

    // exercise sut
    String[] actual = sut.randomArray();

    // verify outcome
    assertThat(actual).contains(element1, element2, element3);

  }

  @Test
  public void testRandomArray_with_size() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";

    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);
    int expectedSize = 2;

    // exercise sut
    String[] actual = sut.randomArray(expectedSize);

    // verify outcome
    assertThat(actual).hasSize(expectedSize);

  }

  @Test
  public void testSet() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";
    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);

    // exercise sut
    Set<String> actual = sut.set();

    // verify outcome
    assertThat(actual).contains(element1, element2, element3).hasSize(3);
  }

  @Test
  public void testSet_with_size() {
    // setup fixtures
    String element1 = "an element";
    String element2 = "other element";
    String element3 = "yet another element";
    GenericDataSet<String> sut = Fixtures.createDataSetOf(element1, element2, element3);
    int expected = 1;
    // exercise sut
    Set<String> actual = sut.set(expected);

    // verify outcome
    assertThat(actual).contains(element1)
        .hasSize(expected);
  }

  @Test
  public void testAny() {
    // setup fixtures

    GenericDataSet<String> sut = Fixtures.createAnonymousDataSet(5);

    // exercise sut
    String actual = sut.any();

    // verify outcome
    assertThat(sut.get()).contains(actual);
  }

  @Test
  public void testAny_with_filter() {
    // setup fixtures

    GenericDataSet<String> sut = Fixtures.createDataSetOf("a","b","c");
    Predicate<String> filter=or(equalTo("b"),equalTo("c"));

    // exercise sut
    String actual = sut.any(filter);

    // verify outcome
    assertThat(actual).isIn("b","c");
  }

  @Test
  public void testLink() {
    // setup fixtures
    GenericDataSet<String> sut = Fixtures.createAnonymousDataSet(5);
    Object linkedObject = new Object();
    String expected = sut.link(linkedObject);

    // exercise sut
    String actual = sut.link(linkedObject);

    // verify outcome
    assertThat(actual).isNotNull()
        .isEqualTo(expected);
  }

  @Test
  public void testReverseLink() {
    // setup fixtures
    GenericDataSet<String> sut = Fixtures.createAnonymousDataSet(5);
    Integer linkedObject = new Integer(12);
    String expected = sut.link(linkedObject);

    // link another object, this should not impact the result.
    sut.link(new Object());

    // exercise sut
    Iterable<Integer> actual = sut.reverseLink(Integer.class, expected);

    // verify outcome
    assertThat(actual).contains(linkedObject);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testEquals() {

    // setup fixtures
    DataSet<String> ds1 = Fixtures.createAnonymousDataSet(5);
    DataSet<String> ds2 = new GenericDataSet<String>(Suppliers.ofInstance(ds1.list()), String.class,RandomBuilder.PROVIDER.singleton());

    // exercise SUT / verify outcome
    assertThat(ds1).isEqualTo(ds1);
    assertThat(ds1).isEqualTo(ds2);
    assertThat(ds2).isEqualTo(ds1);
    assertThat(ds1.hashCode()).isEqualTo(ds2.hashCode());

    // misc assertions

    assertThat(ds1).isNotEqualTo(null);
    assertFalse(ds1.equals(new Object()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testNotEquals() {

    // setup fixtures
    DataSet<String> ds1 = Fixtures.createAnonymousDataSet(5);
    DataSet<String> ds2 = new GenericDataSet<String>(Suppliers.ofInstance(ds1.list(4)), String.class,RandomBuilder.PROVIDER.singleton());

    // exercise SUT / verify outcome
    assertThat(ds1).isNotEqualTo(ds2);
    assertThat(ds2).isNotEqualTo(ds1);
  }

  @Test
  public void testSubSet() {

    // setup fixtures
    GenericDataSet<String> sut = Fixtures.createAnonymousDataSet(5);
    DataSet<String> expected = new GenericDataSet<String>(Suppliers.ofInstance(sut.list(3)), String.class,RandomBuilder.PROVIDER.singleton());

    // exercise SUT
    DataSet<String> actual = sut.subset(3);

    // verify outcome
    assertThat(actual.list()).containsAll(expected.list());
    assertThat(actual).isNotNull()
        .isEqualTo(expected);
  }

  @Test
  public void testFilter() {

    // setup fixtures
    GenericDataSet<String> sut = Fixtures.createDataSetOf("a", "abc", "c", "abcd");

    // exercise sut
    DataSet<String> filtered = sut.filter(containsPattern("abc"));

    // verify outcome
    assertThat(filtered.list()).containsExactly("abc", "abcd");
  }

  @Test
  public void testTransform() {

    // setup fixtures
    GenericDataSet<Integer> sut = Fixtures.createDataSetOf(1, 2, 3, 4, 5);

    // exercise sut
    DataSet<String> filtered = sut.transform(Functions.toStringFunction(), String.class);

    // verify outcome
    assertThat(filtered.list()).containsExactly("1", "2", "3", "4", "5");
  }

  @Test
  public void testWithout() {
    // setup fixtures
    GenericDataSet<Integer> sut =  Fixtures.createDataSetOf(1, 2, 3, 4, 5);

    // exercise sut
    List<Integer> actual = sut.without(2,5).list();

    // verify outcome
    assertThat(actual).containsOnly(1,3,4);
  }

  @Test
  public void testWithout_with_collection() {
    // setup fixtures
    GenericDataSet<Integer> sut =  Fixtures.createDataSetOf(1, 2, 3, 4, 5);

    // exercise sut
    List<Integer> actual = sut.without(Arrays.asList(2,5)).list();

    // verify outcome
    assertThat(actual).containsOnly(1,3,4);
  }

  @Test
  public void testTransformWithWithout() {
    // setup fixtures
    GenericDataSet<Integer> sut =  Fixtures.createDataSetOf(1, 2, 3, 4, 5);

    // exercise sut
    List<String> actual = sut.transform(Functions.toStringFunction(), String.class).without("2","5").list();

    // verify outcome
    assertThat(actual).containsOnly("1","3","4");
  }

}
