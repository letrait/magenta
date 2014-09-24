package org.magenta.core.injection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSetNotFoundException;
import org.magenta.QualifiedDataSet;
import org.magenta.annotations.InjectDataSet;
import org.mockito.Mockito;

import com.google.common.base.Suppliers;

public class DataSetFieldHandlerTest<D> {

  @InjectDataSet
  private QualifiedDataSet<String> qualifiedDataSet;

  @InjectDataSet
  private DataSet<String> dataSet;

  @InjectDataSet
  private DataSet missingGenericDataSet;

  @InjectDataSet
  private DataSet<D> genericDataSet;

  @InjectDataSet
  private String notADataSet;

  private DataSet<String> notAnnotated;

  @InjectDataSet("ANIMALS")
  private QualifiedDataSet<String> qualifiedDataSetWithACustomKey;

  @Test
  public void shouldNotHandleFieldThatAreNotAnnotated() throws NoSuchFieldException, SecurityException{
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("notAnnotated");

    DataDomain fixture = mock(DataDomain.class);
    when(fixture.dataset(Mockito.any(DataKey.class))).thenReturn(mock(DataSet.class));

    //exercise sut
    boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));

    //verify outcome
    assertThat(handled).overridingErrorMessage("The field 'notAnnotated' of this test class was not expected to be handled").isFalse();
    assertThat(notAnnotated).isNull();
  }

  @Test
  public void shouldInjectAQualifiedDataSetIntoThisClassPrivateField() throws NoSuchFieldException, SecurityException {
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("qualifiedDataSet");

    DataDomain fixture = mock(DataDomain.class);
    when(fixture.dataset(Mockito.any(DataKey.class))).thenReturn(mock(DataSet.class));

    //exercise sut
    boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));


    //verify outcome
    assertThat(handled).overridingErrorMessage("The field 'aDataSet' of this test class was expected to be handled").isTrue();
    assertThat(qualifiedDataSet).overridingErrorMessage("No dataset injected into field 'aDataset'").isNotNull();

    assertThat(qualifiedDataSet.getKey()).isEqualTo(DataKey.makeDefault(String.class));
  }

  @Test
  public void shouldInjectAQualifiedDataSetWithACustomKeyIntoThisClassPrivateField() throws NoSuchFieldException, SecurityException {
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("qualifiedDataSetWithACustomKey");

    DataDomain fixture = mock(DataDomain.class);
    when(fixture.dataset(Mockito.any(DataKey.class))).thenReturn(mock(DataSet.class));

    //exercise sut
    boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));


    //verify outcome
    assertThat(handled).overridingErrorMessage("The field 'qualifiedDataSetWithACustomKey' of this test class was expected to be handled").isTrue();
    assertThat(qualifiedDataSetWithACustomKey).overridingErrorMessage("No dataset injected into field 'qualifiedDataSetWithACustomKey'").isNotNull();

    assertThat(qualifiedDataSetWithACustomKey.getKey()).isEqualTo(DataKey.makeQualified("ANIMALS", String.class));
  }



  @Test
  public void shouldDelegateToDataDomainWhenAccessingTheDataSet() throws NoSuchFieldException, SecurityException {
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("dataSet");

    DataDomain fixture = mock(DataDomain.class);
    when(fixture.dataset(Mockito.any(DataKey.class))).thenReturn(mock(DataSet.class));

    //exercise sut
    boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));

    //verify outcome
    dataSet.get();

    verify(fixture).dataset(DataKey.makeDefault(String.class));

  }

  @Test
  public void shouldFailGracefullyWhenTheDataSetIsNotFound() throws NoSuchFieldException, SecurityException {
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("dataSet");

    DataDomain fixture = mock(DataDomain.class);
    when(fixture.dataset(Mockito.any(DataKey.class))).thenThrow(new DataSetNotFoundException("not found"));

    //exercise sut
    boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));

    //verify outcome
    try{
      dataSet.get();
      fail("expected a dataset not found error");
    }catch(DataSetNotFoundException dsnf){
      //expected
    }

    verify(fixture).dataset(DataKey.makeDefault(String.class));

  }

  @Test
  public void shouldFailGracefullyWhenTheFixtureIsNotPresent() throws NoSuchFieldException, SecurityException {
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("dataSet");

    DataDomain fixture = null;

    //exercise sut
    boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));

    //verify outcome
    try{
      dataSet.get();
      fail("Should have failed since no fixture were attached");
    }catch(IllegalStateException ise){
      assertThat(ise).hasMessageContaining("Fixture");
    }



  }

  @Test
  public void shouldFailGracefullyWhenTheDataSetFieldIsMissingTheGeneric() throws NoSuchFieldException, SecurityException{
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("missingGenericDataSet");
    DataDomain fixture = mock(DataDomain.class);

    //exercise sut
    try{
      boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));
      fail("Dataset with no type cannot be injected, an error must have been thrown");
    }catch(IllegalStateException ise){
      assertThat(ise).hasMessageContaining("rawtype");
    }
  }

  @Test
  public void shouldFailGracefullyWhenTheDataSetFieldIsGeneric() throws NoSuchFieldException, SecurityException{
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("genericDataSet");
    DataDomain fixture = mock(DataDomain.class);

    //exercise sut
    try{
      boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));
      fail("Dataset with a generic type cannot be injected, an error must have been thrown");
    }catch(IllegalStateException ise){
      assertThat(ise).hasMessageContaining("generic");
    }
  }

  @Test
  public void shouldFailGracefullyWhenTheFieldTypeIsNotADataSet() throws NoSuchFieldException, SecurityException{
    //setup fixtures
    DataSetFieldHandler sut = new DataSetFieldHandler();

    Field f = this.getClass().getDeclaredField("notADataSet");
    DataDomain fixture = mock(DataDomain.class);

    //exercise sut
    try{
      boolean handled = sut.handle(f, this, Suppliers.ofInstance(fixture));
      fail("Field that are not a DataSet cannot be injected, an error must have been thrown");
    }catch(IllegalStateException ise){
      assertThat(ise).hasMessageContaining("DataSet");
    }
  }

}
