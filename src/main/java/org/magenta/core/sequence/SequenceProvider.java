package org.magenta.core.sequence;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.Fixture;
import org.magenta.Sequence;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;

public class SequenceProvider implements Function<Fixture, SequenceIndexMap> {

  private Map<Field, DataKey<?>> keyMap;

  public SequenceProvider(Map<Field, DataKey<?>> keyMap) {
    this.keyMap = keyMap;
  }

  @Override
  public SequenceIndexMap apply(Fixture input) {

    SequenceIndexMap map = new SequenceIndexMap();

    SequenceCoordinator coordinator = new SequenceCoordinator();

    FieldDataKeyMapEntryComparator comparator = new FieldDataKeyMapEntryComparator(input.keys(), keyMap.keySet());

    for (Map.Entry<Field, DataKey<?>> e : Ordering.from(comparator).immutableSortedCopy(keyMap.entrySet())) {
      DataSet<?> dataset = input.dataset(e.getValue());
      if (!dataset.isConstant()) {
        map.put(e.getKey(), nonDeterministicSequence(dataset));
      } else {
        CoordinatedSequence<?> sequence = combinatorySequence(dataset, coordinator);

        map.put(e.getKey(), sequence);

      }
    }

    return map;
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
