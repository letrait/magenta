package org.magenta.core;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.DataDomainManager;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.core.GeneratorImpl;
import org.magenta.random.Randoms;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.base.Suppliers;

@RunWith(MockitoJUnitRunner.class)
public class GeneratorImplTest {

  @Mock
  DataDomainManager<DataSpecification> datasetMap;
  @Mock
  GenerationStrategy<String,DataSpecification> spy;

  @Test
  public void testIsGenerated(){
  	
  	 GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
  	 
  	 //exercise sut
  	 assertThat(sut.isGenerated()).isTrue();
  	
  }
  
  @Test
  public void testAny() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    Iterable<String> expected = Arrays.asList("result");

    when(spy.generate(Mockito.anyInt(), any(DataDomainManager.class))).thenReturn(expected);

    // exercise sut
    String result = sut.any();

    // verify outcome
    verify(spy).generate(1, datasetMap);
    assertThat(result).isEqualTo(expected.iterator()
        .next());
  }
  
  @Test
  public void testArray() {
  	// setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    String[] expected =new String[]{"1", "2", "3"};

    when(spy.generate(any(DataDomainManager.class))).thenReturn(Arrays.asList(expected));

    // exercise sut
    String[] actual = sut.array();

    // verify outcome
    verify(spy).generate(datasetMap);
    assertThat(actual).containsExactly(expected)
        .hasSameSizeAs(expected);
  }

  @Test
  public void testArray_with_size() {
 // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    String[] expected =new String[]{"1", "2"};

    when(spy.generate(Mockito.anyInt(), any(DataDomainManager.class))).thenReturn(Arrays.asList("1","2"));

    // exercise sut
    String[] actual = sut.array(2);

    // verify outcome
    verify(spy).generate(2,datasetMap);
    assertThat(actual).containsExactly(expected)
        .hasSameSizeAs(expected);
  }

  @Test
  public void testRandomArray() {
 // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    String[] expected =new String[]{"1", "2", "3"};

    when(spy.generate(any(DataDomainManager.class))).thenReturn(Arrays.asList(expected));

    // exercise sut
    String[] actual = sut.randomArray();

    // verify outcome
    verify(spy).generate(datasetMap);
    assertThat(actual).containsOnly(expected)
        .hasSameSizeAs(expected);

  }

  @Test
  public void testRandomArray_with_size() {
  	 // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    String[] expected =new String[]{"1", "2"};

    when(spy.generate(Mockito.anyInt(),any(DataDomainManager.class))).thenReturn(Arrays.asList("1","2"));

    // exercise sut
    String[] actual = sut.randomArray(2);

    // verify outcome
    verify(spy).generate(2,datasetMap);
    assertThat(actual).containsOnly(expected)
        .hasSameSizeAs(expected);

  }

  @Test
  public void testList() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    List<String> expected = Arrays.asList("1", "2", "3");

    when(spy.generate(any(DataDomainManager.class))).thenReturn(expected);

    // exercise sut
    List<String> actual = sut.list();

    // verify outcome
    verify(spy).generate(datasetMap);
    assertThat(actual).containsExactly("1", "2", "3")
        .hasSameSizeAs(expected);
  }

  @Test
  public void testList_with_size() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    List<String> expected = Arrays.asList("1", "2");
    int expectedSize = 2;

    when(spy.generate(Mockito.anyInt(), any(DataDomainManager.class))).thenReturn(expected);

    // exercise sut
    List<String> actual = sut.list(expectedSize);

    // verify outcome
    verify(spy).generate(expectedSize, datasetMap);
    assertThat(actual).containsExactly("1", "2")
        .hasSameSizeAs(expected);
  }

  @Test
  public void testRandomList() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    List<String> expected = Arrays.asList("1", "2", "3");

    when(spy.generate(any(DataDomainManager.class))).thenReturn(expected);

    // exercise sut
    List<String> actual = sut.randomList();

    // verify outcome
    verify(spy).generate(datasetMap);
    assertThat(actual).containsAll(expected)
        .hasSameSizeAs(expected);

  }

  @Test
  public void testRandomList_with_size() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    List<String> expected = Arrays.asList("1", "2");
    int expectedSize = 2;

    when(spy.generate(Mockito.anyInt(), any(DataDomainManager.class))).thenReturn(expected);

    // exercise sut
    List<String> actual = sut.randomList(expectedSize);

    // verify outcome
    verify(spy).generate(expectedSize, datasetMap);
    assertThat(actual).containsAll(expected)
        .hasSameSizeAs(expected);

  }

  @Test
  public void testSet() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    List<String> expected = Arrays.asList("1", "2", "3");

    when(spy.generate(any(DataDomainManager.class))).thenReturn(expected);

    // exercise sut
    Set<String> actual = sut.set();

    // verify outcome
    verify(spy).generate(datasetMap);
    assertThat(actual).containsAll(expected)
        .hasSameSizeAs(expected);
  }

  @Test
  public void testSet_with_size() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);
    List<String> expected = Arrays.asList("1", "2");
    int expectedSize = 2;

    when(spy.generate(Mockito.anyInt(), any(DataDomainManager.class))).thenReturn(expected);

    // exercise sut
    Set<String> actual = sut.set(expectedSize);

    // verify outcome
    verify(spy).generate(expectedSize, datasetMap);
    assertThat(actual).containsAll(expected)
        .hasSize(expectedSize);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testLink() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);

    // exercise sut
    String actual = sut.link(new Object());

  }

  @Test(expected = UnsupportedOperationException.class)
  public void testReverseLink() {
    // setup fixtures
    GeneratorImpl<String,DataSpecification> sut = createGeneratorImpl(spy);

    // exercise sut
    sut.reverseLink(String.class, "Test");
  }

  

  private GeneratorImpl<String,DataSpecification> createGeneratorImpl(GenerationStrategy<String,DataSpecification> strategy) {
    return new GeneratorImpl<String,DataSpecification>(datasetMap, strategy, String.class);
  }
}
