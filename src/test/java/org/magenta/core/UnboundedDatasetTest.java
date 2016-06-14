package org.magenta.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.magenta.DataSet;
import org.magenta.DataSupplier;
import org.magenta.UnboundedDataSetException;
import org.magenta.core.data.supplier.LazyGeneratedDataSupplier;
import org.magenta.random.FluentRandom;

import com.google.common.reflect.TypeToken;

public class UnboundedDatasetTest {


  @Test(expected=UnboundedDataSetException.class)
  public void testAnyOnAnUnbondedDataSetShouldThrownException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.any();

  }

  @Test(expected=UnboundedDataSetException.class)
  public void testAnyWithPredicateOnAnUnbondedDataSetShouldThrownException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.any(s->true);

  }

  @Test(expected=UnboundedDataSetException.class)
  public void testArrayOnAnUnbondedDataSetShouldThrownException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.array();

  }

  @Test
  public void testArrayWithSizeOnAnUnbondedDataSetShouldWork(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    assertThat(dataset.array(10)).hasSize(10);
  }

  @Test(expected=UnboundedDataSetException.class)
  public void testFilteringOnAnUnbondedDataSetShouldThrowException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.filter(s->false).any();
  }

  @Test(expected=UnboundedDataSetException.class)
  public void testFreezeOnAnUnbondedDataSetShouldThrowException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.freeze();
  }

  @Test(expected=UnboundedDataSetException.class)
  public void testListOnAnUnbondedDataSetShouldThrowException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.list();
  }

  @Test(expected=UnboundedDataSetException.class)
  public void testRandomArrayOnAnUnbondedDataSetShouldThrowException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.randomArray();
  }

  @Test(expected=UnboundedDataSetException.class)
  public void testRandomListOnAnUnbondedDataSetShouldThrowException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.randomList();
  }

  @Test(expected=UnboundedDataSetException.class)
  public void testSetOnAnUnbondedDataSetShouldThrowException(){

    //setup
    DataSet<String> dataset = createUnboundedDataSet();

    //exercise
    dataset.set();
  }



  private DataSet<String> createUnboundedDataSet() {
    DataSupplier<String> supplier = new LazyGeneratedDataSupplier<>(TypeToken.of(String.class), ()->FluentRandom.strings().charabia(15), ()->Integer.MAX_VALUE);

    DataSet<String> dataset = new DataSetImpl<>(supplier, true);
    return dataset;
  }

}
