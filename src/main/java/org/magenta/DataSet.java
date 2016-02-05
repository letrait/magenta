package org.magenta;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public interface DataSet<D> extends DataSupplier<D>{

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
  DataSet<D> resize(int size);

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
  

  public <S> DataSet<S> cast(Class<S> superType);


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
   * TODO: put in an "inner" interface, do not expose
   *
   * @param filter
   *          the filter
   * @return a randomly selected item
   */
  @Override
  public D get(int position);

  /**
   * Return the first element of this dataset.
   *
   * @return the first item
   */
  public D first();




  /**
   * @return true if this dataset is empty
   */
  @Override
  public boolean isEmpty();



}
