package org.magenta.testing;

import org.magenta.DataSet;

public class DataSetAssert<D> extends AbstractDataSetAssert<DataSetAssert<D>, D> {
  protected DataSetAssert(DataSet<D> actual) {
    super(actual, DataSetAssert.class);
  }

}
