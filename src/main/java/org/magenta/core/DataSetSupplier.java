package org.magenta.core;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.Fixture;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

public class DataSetSupplier<D> implements Supplier<DataSet<D>> {

  private final DataKey<D> key;
  private final Supplier<? extends Fixture> fixture;

  public static <D> DataSetSupplier<D> forKey(final DataKey<D> key, final Supplier<? extends Fixture> fixture) {
    return new DataSetSupplier<D>(key, fixture);

  }

  private DataSetSupplier(final DataKey<D> key, final Supplier<? extends Fixture> fixture) {
    this.key = key;
    this.fixture = fixture;
  }

  @Override
  public DataSet<D> get() {
    return Preconditions.checkNotNull(fixture.get(), "No fixture in the current context").dataset(key);
  }

  @Override
  public String toString() {
    return key.toString();
  }

}
