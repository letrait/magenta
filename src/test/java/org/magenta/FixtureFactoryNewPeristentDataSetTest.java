package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

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
    sut.setDataStoreProvider(provider);

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
    sut.setDataStoreProvider(provider);

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
       return (String)invocation.getArguments()[0];
      }

    });

    FixtureFactory<SimpleDataSpecification> sut = createAnonymousFixtureFactory();
    sut.setDataStoreProvider(provider);

    sut.newDataSet(Integer.class).composedOf(1,2,3);
    sut.newDataSet(String.class).persistent().composedOf("a","b","c");

    //exercise sut
    String actual = sut.restrictTo(1).dataset(String.class).any();

    //verify outcome
    assertThat(actual).isIn("a","b","c");
    verify(provider).get(key);
    verify(datastore).persist(actual);
    verifyNoMoreInteractions(datastore);
  }

}
