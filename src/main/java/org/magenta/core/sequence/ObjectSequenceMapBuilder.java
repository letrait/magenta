package org.magenta.core.sequence;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.assertj.core.util.Maps;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSetNotFoundException;
import org.magenta.Fixture;
import org.magenta.Sequence;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class ObjectSequenceMapBuilder implements Function<Fixture, ObjectSequenceMap> {

  private Map<Field, DataKey<?>> keyMap;
  public ObjectSequenceMapBuilder(Map<Field, DataKey<?>> keyMap) {
    this.keyMap = keyMap;
  }

  @Override
  public ObjectSequenceMap apply(Fixture fixture) {

   Map<Field, Sequence<?>> map =Maps.newHashMap();

    SequenceCoordinator coordinator = new SequenceCoordinator();

    List<DataKey<?>> keys= Lists.newArrayList(fixture.keys());

    if(!keys.containsAll(keyMap.values())){
      throw new DataSetNotFoundException(String.format("fixture is missing some or all of the following keys %s", keyMap.values()));
    }

    FieldDataKeyMapEntryComparator comparator = new FieldDataKeyMapEntryComparator(keys, keyMap.keySet());

    for (Map.Entry<Field, DataKey<?>> e : Ordering.from(comparator).immutableSortedCopy(keyMap.entrySet())) {

      DataKey<?> datakey = generalize(e.getValue(), keys);

      DataSet<?> dataset = fixture.dataset(datakey);
      if (!dataset.isConstant()) {
        map.put(e.getKey(), nonDeterministicSequence(dataset));
      } else {
        CoordinatedSequence<?> sequence = combinatorySequence(dataset, coordinator);

        map.put(e.getKey(), sequence);

      }
    }
    
    ObjectSequenceMap sim = new ObjectSequenceMap(map,coordinator.numberOfCombination());

    return sim;
  }

  /**
   * Takes the generalized version (a datakey without qualifier) of a given datakey only if that generalized version is placed before this given key in the
   * complete list of data keys.
   *
   *
   * @param datakey
   * @param keys
   * @return
   */
  private DataKey<?> generalize(DataKey<?> datakey, List<DataKey<?>> keys) {
    if (!datakey.isGeneralized() && datakey.isGeneralizable()) {
      int i1 = keys.indexOf(datakey);
      int i2 = keys.indexOf(datakey.generalize());

      if (i2 != -1 && i2 < i1) {
        datakey = datakey.generalize();
      }
    }
    return datakey;
  }

  private <D> CoordinatedSequence<D> combinatorySequence(DataSet<D> dataset, SequenceCoordinator coordinator) {
    return new CoordinatedSequence<D>(dataset, coordinator);
  }

  private <D> Sequence<D> nonDeterministicSequence(final DataSet<D> dataset) {
    return new Sequence<D>() {

      @Override
      public D get() {
        return dataset.any();
      }

      @Override
      public int size(){
        return 1;
      }


    };
  }

  private static class FieldDataKeyMapEntryComparator implements Comparator<Map.Entry<Field, DataKey<?>>> {

    private Map<DataKey<?>, Integer> dataKeyRankMap;
    private Map<Field, Integer> fieldRankMap;

    FieldDataKeyMapEntryComparator(Iterable<DataKey<?>> keysInOrder, Iterable<Field> fieldInOrder) {
      this.dataKeyRankMap = buildRankMap(keysInOrder);
      this.fieldRankMap = buildRankMap(fieldInOrder);
    }

    private <D> Map<D, Integer> buildRankMap(Iterable<D> valuesInOrder) {

      ImmutableMap.Builder<D, Integer> builder = ImmutableMap.builder();
      int rank = 0;
      for (D value : valuesInOrder) {
        builder.put(value, rank++);
      }
      return builder.build();

    }

    @Override
    public int compare(Entry<Field, DataKey<?>> o1, Entry<Field, DataKey<?>> o2) {
      int comparison = dataKeyRankMap.get(o1.getValue()).compareTo(dataKeyRankMap.get(o2.getValue()));
      if (comparison == 0) {
        return fieldRankMap.get(o1.getKey()).compareTo(fieldRankMap.get(o2.getKey()));
      } else {
        return comparison;
      }
    }

  }

}
