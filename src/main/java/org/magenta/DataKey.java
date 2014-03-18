package org.magenta;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.magenta.core.EmptyDataSet;
import org.magenta.core.ForwardingDataSet;
import org.magenta.core.GenericDataSet;
import org.magenta.random.Randoms;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;

/**
 * A DataKey is used to reference a {@link DataSet}, {@link Generator} or a
 * {@link GenerationStrategy}. There are two types of key : default and
 * qualified. A default key is one created with just a class name, so it will be
 * considered as the "default" key for this class. A qualified key add a
 * qualifier to the key type so two keys referring to the same type can be
 * distinguished.
 *
 * @author ngagnon
 *
 * @param <D>
 *          the data type of the referenced dataset, generator or generation
 *          strategy.
 */
public class DataKey<D> {

  public static final String DEFAULT_QUALIFIER = "default";
  private final String qualifier;
  private final Class<D> type;

  DataKey(String qualifier, Class<D> type) {
    this.qualifier = checkNotNull(qualifier);
    this.type = checkNotNull(type);
  }

  /**
   * Make a new key for the given qualifier and the class.
   *
   * @param qualifier
   *          a string that disambiguate this key from another that is of the
   *          same type.
   * @param type
   *          the type of data
   * @param <D>
   *          the type of data
   * @return a new key
   */
  public static <D> DataKey<D> makeQualified(String qualifier, Class<D> type) {
    DataKey<D> ref = new DataKey<D>(qualifier, type);
    return ref;
  }

  /**
   * Make a new key for the given class using the default qualifier key.
   *
   * @param type
   *          the type of data
   * @param <D>
   *          the type of data
   * @return a new key
   */
  public static <D> DataKey<D> makeDefault(Class<D> type) {
    DataKey<D> ref = new DataKey<D>(DEFAULT_QUALIFIER, type);
    return ref;
  }

  /**
   * Return true if this key is the default for this key type.
   *
   * @return true if it is a default key.
   */
  public boolean isDefault() {
    return DEFAULT_QUALIFIER.equals(qualifier);
  }

  /**
   * @return the category
   */
  public String getQualifier() {
    return this.qualifier;
  }

  /**
   * @return the type
   */
  public Class<D> getType() {
    return this.type;
  }

  /**
   * Get the {@link DataSet} in the <code>domain</code> identified by this key.
   *
   *
   * @param domain the domain
   * @return the matching data set
   */
  public DataSet<D> getDataSetFrom(DataDomain<?> domain) {
    return domain.dataset(this);
  }

  /**
   * Return a {@link Qualified} dataset.
   *
   * @param randomizer
   *          the randomizer to use
   * @param data
   *          the data composing the qualified data set.
   * @return a new QualifiedDataSet
   */

  @SuppressWarnings("unchecked")
  public QualifiedDataSet<D> asDataSet(Randoms randomizer, D... data) {
    return new QualifiedDataSetImpl<D>(this, new GenericDataSet<D>(Suppliers.ofInstance(Arrays.asList(data)), this.type, randomizer));
  }

  /**
   *
   * @return an empty data set for this key
   */
  public EmptyDataSet<D> asEmptyDataSet() {
    return EmptyDataSet.ofType(getType());
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (type.hashCode());
    result = prime * result + (qualifier.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {

      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DataKey)) {
      return false;
    }
    DataKey<?> other = (DataKey<?>) obj;
    if (!type.equals(other.getType())) {
      return false;
    }
    if (!qualifier.equals(other.getQualifier())) {

      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(getType().getName()).append(':').append(getQualifier());
    return builder.toString();
  }

  static class QualifiedDataSetImpl<D2> extends ForwardingDataSet<D2> implements QualifiedDataSet<D2> {

    private final DataKey<D2> key;

    private QualifiedDataSetImpl(DataKey<D2> qualifier, DataSet<D2> delegate) {
      super(delegate);
      this.key = qualifier;
    }

    @Override
    public DataKey<D2> getKey() {
      return key;
    }

  }

  /**
   * Utility function that creates a default {@link DataKey} for a given class.
   *
   * @return the function
   */
  @SuppressWarnings("rawtypes")
  public static Function<Class, DataKey> classToKey() {
    return new Function<Class, DataKey>() {

      @SuppressWarnings("unchecked")
      @Override
      public DataKey apply(Class input) {
        return DataKey.makeDefault(input);
      }

    };
  }

}
