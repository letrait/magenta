package org.magenta.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.magenta.DataSet;
import org.magenta.random.FluentRandom;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of a {@link DataSet} based on a guava {@link Supplier} of
 * Iterable.
 *
 * @author ngagnon
 *
 * @param <D>
 *          the data type
 */
public abstract class AbstractDataSet<D> implements DataSet<D> {

  private final Map<Object, D> links;

  private final Class<D> type;

  private final FluentRandom randomizer;

  /**
   * Construct a new dataset using the provided supplier as source.
   *
   * @param type
   *          the type of data
   * @param randomizer
   *          the java random to use for shuffling and by the "any()" method
   */
  public AbstractDataSet(Class<D> type, FluentRandom randomizer) {
    this.links = new LinkedHashMap<>();
    this.type = type;
    this.randomizer = randomizer;
  }

  @Override
  public boolean isEmpty() {
    return !get().iterator().hasNext();
  }



  @Override
  public DataSet<D> toTransient() {
   return this;
  }

  @Override
  public DataSet<D> subset(int size) {
    return new GenericDataSet<>(Iterables.limit(get(), size), this.type, getRandomizer());
  }

  @Override
  public DataSet<D> filter(Predicate<? super D> filter) {
    return new GenericDataSet<>(Iterables.filter(get(),  o -> filter.test(o)), this.type, getRandomizer());
  }

  @Override
  public <X> DataSet<X> transform(Function<? super D, X> function, Class<X> newType) {
    return new GenericDataSet<>(Iterables.transform(get(), i -> function.apply(i)), newType, getRandomizer());
  }

  @Override
  public <X> DataSet<X> cast(final Class<X> type) {

    return transform( new Function<D,X>(){

      @Override
      public X apply(D input) {
        return type.cast(input);
      }

    }, type);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.magenta.DataSet#process(org.magenta
   * .Processor)
   */
  /*
   * @Override public DataSet<D> process(final Function<? super D, Void>
   * processor) { return new GenericDataSet<D>(Iterables.transform(get(), new
   * Function<D, D>() {
   *
   * @Override public D apply(D input) { processor.apply(input); return input; }
   *
   * }), this.type, getRandomizer()); }
   */

  @Override
  @SafeVarargs
  public final DataSet<D> without(D... items) {
    Predicate<D> filter = i -> Arrays.asList(items).contains(i);
    return filter(filter.negate());
  }

  @Override
  public DataSet<D> without(Collection<D> items) {
    Predicate<D> filter = i -> items.contains(i);
    return filter(filter.negate());
  }

  @Override
  public abstract Iterable<D> get();

  @Override
  public D[] array() {
    return Iterables.toArray(get(), this.type);
  }

  @Override
  public D[] array(int size) {
    return Iterables.toArray(list(size), this.type);
  }

  @Override
  public D[] randomArray() {
    return Iterables.toArray(randomList(), this.type);
  }

  @Override
  public D[] randomArray(int size) {
    return Iterables.toArray(randomList(size), this.type);
  }

  @Override
  public List<D> list() {
    Iterable<D> i = get();

    return (i instanceof List)?(List<D>)i :Lists.newArrayList(i);
  }

  @Override
  public List<D> list(int size) {
    Iterable<D> i = Iterables.limit(get(), size);
    return i instanceof List? (List<D>)i:Lists.newArrayList(i);
  }

  @Override
  public List<D> randomList() {
    return getRandomizer().iterable(get()).shuffle().list();
  }

  @Override
  public List<D> randomList(int size) {
    return Lists.newArrayList(Iterables.limit(getRandomizer().iterable(get()).shuffle().list(), size));
  }

  @Override
  public Set<D> set() {
    return Sets.newLinkedHashSet(get());
  }

  @Override
  public Set<D> set(int size) {
    return Sets.newLinkedHashSet(Iterables.limit(get(), size));
  }

  @Override
  public D any() {
    return getRandomizer().iterable(get()).any();
  }

  @Override
  public D any(Predicate<? super D> filter) {
    return getRandomizer().iterable(Iterables.filter(get(), i -> filter.test(i))).any();
  }

  @Override
  public DataSet<D> load(){
    for(D data:get()){
    }

    return this;
  }

  @Override
  public DataSet<D> persist() {
    throw new UnsupportedOperationException("This dataset is not persistent:"+toString());

  }

  @Override
  public D link(Object o) {
    D referred = links.get(o);

    if (referred == null) {
      referred = any();
      links.put(o, referred);
    }

    return referred;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <L> Iterable<L> reverseLink(Class<L> type, D referred) {
    List<L> linkedObjects = Lists.newArrayList();
    for (Map.Entry<Object, D> entry : links.entrySet()) {
      if (entry.getValue().equals(referred)) {
        linkedObjects.add((L) entry.getKey());
      }
    }
    return linkedObjects;
  }

  @Override
  public Class<D> getType() {
    return this.type;
  }

  /**
   * @return this data set randomizer
   */
  protected FluentRandom getRandomizer() {
    return randomizer;
  }

  @Override
  public int hashCode() {
    return !this.isGenerated() && !this.isPersistent() ? get().hashCode() : super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (obj instanceof DataSet) {
      @SuppressWarnings("unchecked")
      DataSet<D> other = (DataSet<D>) obj;

      return !this.isGenerated() && !other.isGenerated() && !this.isPersistent() && !other.isPersistent() && Iterables.elementsEqual(this.get(), other.get());
    }
    return false;
  }

}
