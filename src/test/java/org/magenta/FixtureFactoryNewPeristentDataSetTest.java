package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.magenta.annotations.InjectDataSet;
import org.magenta.annotations.InjectFluentRandom;
import org.magenta.random.FluentRandom;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;

@RunWith(MockitoJUnitRunner.class)
public class FixtureFactoryNewPeristentDataSetTest extends FixtureFactoryTestSupport {

  @Mock
  private DataStoreProvider provider;
  @Mock
  private DataStore<String> datastore;

  @Test
  public void testDataIsPersistedAsRequested(){

    //setup fixtures
    DataKey<String> key = DataKey.makeDefault(String.class);
    when(provider.get(Mockito.any(DataKey.class))).thenReturn(datastore);
    when(datastore.persist(Mockito.any(String.class))).thenAnswer(new Answer<String>(){

      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
       return (String)invocation.getArguments()[0];
      }

    });

    FixtureFactory<SimpleDataSpecification> sut = createAnonymousFixtureFactory();
    sut.setDataStoreProvider(provider, true);

    sut.newDataSet(String.class).persistent().composedOf("a","b","c");

    //exercise sut
    String actual = sut.dataset(String.class).list().get(2);

    //verify outcome
    assertThat(actual).isEqualTo("c");
    verify(provider).get(key);
    verify(datastore).persist("c");
    verifyNoMoreInteractions(datastore);
  }

  @Test
  public void testDataIsPersistedAsRequestedOnAny(){

    //setup fixtures
    DataKey<String> key = DataKey.makeDefault(String.class);
    when(provider.get(Mockito.any(DataKey.class))).thenReturn(datastore);
    when(datastore.persist(Mockito.any(String.class))).thenAnswer(new Answer<String>(){

      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
       return (String)invocation.getArguments()[0];
      }

    });

    FixtureFactory<SimpleDataSpecification> sut = createAnonymousFixtureFactory();
    sut.setDataStoreProvider(provider, true);


    sut.newDataSet(String.class).persistent().composedOf("a","b","c");

    //exercise sut
    String actual = sut.dataset(String.class).any();

    //verify outcome
    assertThat(actual).isIn("a","b","c");
    verify(provider).get(key);
    verify(datastore).persist(actual);
    verifyNoMoreInteractions(datastore);
  }

  @Test
  public void testDataIsPersistedAsRequestedOnAnyUnderARestriction(){

    //setup fixtures
    DataKey<String> key = DataKey.makeDefault(String.class);
    when(provider.get(Mockito.any(DataKey.class))).thenReturn(datastore);
    when(datastore.persist(Mockito.any(String.class))).thenAnswer(new Answer<String>(){

      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        System.out.println("persisting "+(String)invocation.getArguments()[0]);
       return (String)invocation.getArguments()[0];
      }

    });

    FixtureFactory<SimpleDataSpecification> sut = createAnonymousFixtureFactory();
    sut.setDataStoreProvider(provider, true);

    sut.newDataSet(Integer.class).composedOf(1,2,3);
    sut.newDataSet(String.class).persistent().filtered(Predicates.alwaysTrue()).composedOf("a","b","c");

    //exercise sut

    String actual = sut.restrictTo(1).dataset(String.class).any();

    //verify outcome
    assertThat(actual).isIn("a","b","c");
    verify(provider).get(key);
    verify(datastore).persist(actual);
    verifyNoMoreInteractions(datastore);
  }

  @Test
  @Ignore
  public void testDataIsPersistedAsRequestedOnAMaterializedDataSet(){

    //setup fixtures
    DataKey<String> key = DataKey.makeDefault(String.class);
    when(provider.get(Mockito.any(DataKey.class))).thenReturn(datastore);
    when(datastore.persist(Mockito.any(String.class))).thenAnswer(new Answer<String>(){

      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
       System.out.println("persisting "+(String)invocation.getArguments()[0]);
       return (String)invocation.getArguments()[0];
      }

    });

    when(datastore.retrieve(Mockito.any(String.class))).thenAnswer(new Answer<String>(){

      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
       System.out.println("retrieving  "+(String)invocation.getArguments()[0]);
       return (String)invocation.getArguments()[0];
      }

    });

    FixtureFactory<SimpleDataSpecification> sut = createAnonymousFixtureFactory();
    sut.setDataStoreProvider(provider, true);

    DataKey<String> numbers = DataKey.makeQualified("numbers", String.class);
    DataKey<String> letters = DataKey.makeQualified("letters", String.class);

    sut.newDataSet(numbers).persistent().composedOf("1","2","3");
    sut.newDataSet(letters).persistent().composedOf("a","b","c");
    sut.newDataSet(String.class).generatedAsIterableBy(new MaterializedDataSetExample());

    //exercise sut
    String actual = sut.dataset(String.class).any();

    //verify outcome
    assertThat(actual).isIn("1","2","3","a","b","c");

    verify(datastore).persist(actual);
    verifyNoMoreInteractions(datastore);
  }

  public class MaterializedDataSetExample implements Supplier<Iterable<String>> {

    @InjectDataSet("numbers")
    DataSet<String> numbers;

    @InjectDataSet("letters")
    DataSet<String> letters;

    @InjectFluentRandom
    private FluentRandom rnd;

    @Override
    public Iterable<String> get() {

      return numbers.get();
      //return Iterables.concat(numbers.get(),letters.get());
    }

  }

}
