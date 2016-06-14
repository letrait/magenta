package org.magenta.core;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.Fixture;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class DataSetSupplier<D> implements Supplier<DataSet<D>> {

  private final DataKey<D> key;
  private final Supplier<? extends Fixture> fixture;
  private final LoadingCache<CacheKey<D>,DataSet<D>> cache;

  public static <D> DataSetSupplier<D> forKey(final DataKey<D> key, final Supplier<? extends Fixture> fixture) {
    return new DataSetSupplier<D>(key, fixture);
  }

  private DataSetSupplier(final DataKey<D> key, final Supplier<? extends Fixture> fixture) {
    this.key = key;
    this.fixture = fixture;
    this.cache = CacheBuilder.newBuilder().build(CacheLoader.<CacheKey<D>,DataSet<D>>from(k->k.getFixture().dataset(k.getKey())));
  }

  @Override
  public DataSet<D> get() {
    return cache.getUnchecked(new CacheKey<D>(Preconditions.checkNotNull(fixture.get(), "No fixture in the current context"),this.key));
  }

  @Override
  public String toString() {
    return key.toString();
  }

  private static class CacheKey<D>{
    private final Fixture fixture;
    private final DataKey<D> key;

    public CacheKey(Fixture fixture, DataKey<D> key) {
      super();
      this.fixture = fixture;
      this.key = key;
    }

    public Fixture getFixture() {
      return fixture;
    }
    public DataKey<D> getKey() {
      return key;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fixture == null) ? 0 : fixture.hashCode());
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CacheKey other = (CacheKey) obj;
      if (fixture == null) {
        if (other.fixture != null)
          return false;
      } else if (!fixture.equals(other.fixture))
        return false;
      if (key == null) {
        if (other.key != null)
          return false;
      } else if (!key.equals(other.key))
        return false;
      return true;
    }


  }

}
