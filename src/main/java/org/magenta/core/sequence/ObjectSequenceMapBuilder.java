package org.magenta.core.sequence;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.Fixture;
import org.magenta.Sequence;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class ObjectSequenceMapBuilder implements Function<Fixture, ObjectSequenceMap> {

  private Iterable<FieldSequenceDefinition> definitions;
  public ObjectSequenceMapBuilder(Iterable<FieldSequenceDefinition> definitions) {
    this.definitions = definitions;
  }

  @Override
  public ObjectSequenceMap apply(Fixture fixture) {

    Map<Field, Sequence<?>> map = Maps.newLinkedHashMap();

    //there will be a coordinator for each group of sequence, one for the unique index
    SequenceCoordinator coordinator = new SequenceCoordinator();

    List<DataKey<?>> keys= Lists.newArrayList(fixture.keys());

    /*if(!keys.containsAll(keyMap.values())){
      throw new DataSetNotFoundException(String.format("fixture is missing some or all of the following keys %s", keyMap.values()));
    }*/

    FieldDataKeyMapEntryComparator comparator = new FieldDataKeyMapEntryComparator(keys, FluentIterable.from(definitions).transform(d->d.getField()));

    for (FieldSequenceDefinition e : Ordering.from(comparator).immutableSortedCopy(definitions)) {

      //TODO : Field can be either a MAgenta object such as Sequence or DataSet, or java type, such as String, Integer
      //There are two different use cases that are being represented by the same ObjectSequenceInfoByField class

      DataKey<?> datakey = generalize(e.getKey(), keys);

      DataSet<?> dataset = fixture.dataset(datakey);
      if (!dataset.isConstant()) {
        map.put(e.getField(), nonDeterministicSequence(dataset));
      } else {
        CoordinatedSequence<?> sequence = combinatorySequence(dataset, e.getType() == FieldSequenceDefinition.Type.UNIQUE_SEQUENCE, coordinator);

        map.put(e.getField(), sequence);

      }
    }

    //la taille sera determine selon different coordinator... celui de l'unique index par example, si il existe plus d'un groupe sans qi'il y ait une contrainte d'unicite, alors on
    //prend la taille du plus grand coordinator, avec contrainte, on prend le plus petit SequenceCoordinator contraint.
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
  private static DataKey<?> generalize(DataKey<?> datakey, List<DataKey<?>> keys) {
    if (!datakey.isGeneralized() && datakey.isGeneralizable()) {
      int i1 = keys.indexOf(datakey);
      int i2 = keys.indexOf(datakey.generalize());

      if (i2 != -1 && (i1 == -1 || i2 < i1)) {
        datakey = datakey.generalize();
      }
    }
    return datakey;
  }

  private boolean isAnIterable(FieldSequenceDefinition e) {
    return Iterable.class.isAssignableFrom(e.getField().getType()) || e.getField().getType().isArray();
  }

  private <D> CoordinatedSequence<D> combinatorySequence(DataSet<D> dataset, boolean unique, SequenceCoordinator coordinator) {
    return new CoordinatedSequence<D>(dataset, unique, coordinator);
  }

  private <D> Sequence<D> nonDeterministicSequence(final DataSet<D> dataset) {
    return new Sequence<D>() {

      @Override
      public D next() {
        return dataset.any();
      }

      @Override
      public int size(){
        return 1;
      }
    };
  }

  private static class FieldDataKeyMapEntryComparator implements Comparator<FieldSequenceDefinition> {

    private Map<DataKey<?>, Integer> dataKeyRankMap;
    private Map<Field, Integer> fieldRankMap;
    private List<DataKey<?>> keysIndex;

    FieldDataKeyMapEntryComparator(Iterable<DataKey<?>> keysInOrder, Iterable<Field> fieldInOrder) {
      this.dataKeyRankMap = buildRankMap(keysInOrder);
      this.fieldRankMap = buildRankMap(fieldInOrder);
      this.keysIndex = Lists.newArrayList(keysInOrder);
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
    public int compare(FieldSequenceDefinition o1, FieldSequenceDefinition o2) {

      //rank comparison must be done on the generalized data key, not the original key because of FixtureFactorySequenceDemonstrationTest#generateAllFaces

      Integer rank1 = dataKeyRankMap.get(generalize(o1.getKey(), keysIndex));
      Integer rank2 = dataKeyRankMap.get(generalize(o2.getKey(), keysIndex));

      //The rank may be null if the key does not exist in the fixture, which will be detected further in the code, but in this
      //place we return a value so no exception is thrown.
      if(rank1==null){
        return -1;
      }else if(rank2==null){
        return 1;
      }

      int comparison = rank1.compareTo(rank2);
      if (comparison == 0) {
        return fieldRankMap.get(o1.getField()).compareTo(fieldRankMap.get(o2.getField()));
      } else {
        return comparison;
      }
    }

  }

}
