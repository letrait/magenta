package org.magenta;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * A DataSet is a collection of item of the same type. This class provides
 * utility methods to manipulate data in the context of test fixture setup.
 *
 * @author ngagnon
 *
 * @param <D>
 *          the type of data wrapped by this instance
 */
public interface DataSet<D> extends Supplier<Iterable<D>> {

  /**
   * @return true if this dataset is generated or false if it is fixed.
   */
  public boolean isGenerated();

  /**
   * @return true if this dataset is persistent or false if it is transient.
   */
  public boolean isPersistent();

  /**
   * @return a transient dataset if this dataset is persistent, no effect if this dataset is already transient
   */
  public DataSet<D> toTransient();


  /**
   * @return this dataset type
   */
  public Class<D> getType();

  /**
   * return this dataset as an array.
   *
   * @return an array
   */
  public D[] array();

  /**
   * Return this dataset as a sub array of the specified size.
   *
   * @param size
   *          the size of the sub array
   * @return an array
   */
  public D[] array(int size);

  /**
   * return this dataset as a shuffled array.
   *
   * @return an array
   */
  public D[] randomArray();

  /**
   * Return this dataset as a sub shuffled array of the specified size.
   *
   * @param size
   *          the size of the sub array
   * @return an array
   */
  public D[] randomArray(int size);

  /**
   * Return this dataset as a list.
   *
   * @return a list
   */
  public List<D> list();

  /**
   * Return this dataset as a sublist of the specified size.
   *
   * @param size
   *          the size of the sublist.
   * @return a sublist
   */
  public List<D> list(int size);

  /**
   * Return this dataset as a shuffled list.
   *
   * @return a shuffled list
   */
  public List<D> randomList();

  /**
   * Return this dataset as a shuffled sublist of the specified size.
   *
   * @param size
   *          the size of the sublist.
   * @return a sublist
   */
  public List<D> randomList(int size);

  /**
   * Return this dataset as a set.
   *
   * @return a set
   */
  public Set<D> set();

  /**
   * Return this dataset as a sub set.
   *
   * @param size
   *          the size of the sub set.
   * @return a sub set.
   */
  public Set<D> set(int size);

  /**
   * Return a new {@link DataSet} which represent a sub set of this instance.
   *
   * @param size
   *          the size of the sub set.
   * @return a sub {@link DataSet}
   */
  public DataSet<D> subset(int size);

  /**
   * Return a new {@link DataSet} having the same data has this one but without
   * the specified items.
   *
   * @param items
   *          the omitted items
   * @return a new data set view
   */
  @SuppressWarnings("unchecked")
  public DataSet<D> without(D... items);

  /**
   * Return a new {@link DataSet} having the same data has this one but without
   * the specified items.
   *
   * @param items
   *          the omitted items
   * @return a new data set view
   */
  public DataSet<D> without(Collection<D> items);

  /**
   * Return a new filtered view of this DataSet.
   *
   * @param filter
   *          the filter
   * @return a filtered DataSet
   */
  public DataSet<D> filter(Predicate<? super D> filter);

  public <S> DataSet<S> cast(Class<S> superType);

  /**
   * Transform this DataSet into another DataSet.
   *
   * @param function
   *          the transformation function
   * @param transformedType
   *          the transformed type
   * @param <X>
   *          the data type of the transformed dataset.
   * @return a transformed dataset.
   */
  public <X> DataSet<X> transform(Function<? super D, X> function, Class<X> transformedType);

  /**
   * Process each item of this dataset using the passed in processor.
   *
   * @param function
   *          the processing function
   * @return this dataset
   */
  // public DataSet<D> process(Function<? super D,Void> processor);

  /**
   * Return a randomly selected item from this dataset.
   *
   * @return a randomly selected item
   */
  public D any();

  /**
   * Return a randomly selected item from this dataset that matches the passed
   * in filter.
   *
   * @param filter
   *          the filter
   * @return a randomly selected item
   */
  public D any(Predicate<? super D> filter);

  /**
   * Link an object to a randomly selected item in this data set. This can be
   * useful to built consistent fixtures such as query result.
   *
   * @param o
   *          the object to which link the item
   * @return the item linked to the specified object or a randomly selected one
   *         if it wasn't yet linked.
   */
  public D link(Object o);

  /**
   * Return an iterable of all objets linked to the specified item.
   *
   * @param type
   *          the type of object
   * @param referred
   *          the item to which objets may have been linked.
   * @param <L>
   *          the type of object.
   * @return an iterable of all objects
   */
  public <L> Iterable<L> reverseLink(Class<L> type, D referred);

  /**
   * @return true if this dataset is empty
   */
  public boolean isEmpty();

  public DataSet<D> persist();

  public DataSet<D> load();



}
