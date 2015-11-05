package org.magenta.core.sequence;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.magenta.Sequence;

/**
 * A collection of sequence associated to a given object. Each sequence is mapped to a field of that given object.
 * 
 * @author ngagnon
 *
 */
public class ObjectSequenceMap {

  private final Map<Field,Sequence<?>> sequenceByField;
  
  private final Integer combinationCount;
  
  public ObjectSequenceMap( Map<Field,Sequence<?>> sequenceIndexByField, Integer combinationCount){
    this.sequenceByField = sequenceIndexByField;
    this.combinationCount = combinationCount;
  }
  
  public Integer getCombinationCount(){
    return this.combinationCount;
  }
  
  public Sequence<?> get(Field field) {
    return sequenceByField.get(field);
  }

  public boolean isEmpty() {
    return sequenceByField.isEmpty();
  }

  public Set<Field> fields() {
    return Collections.unmodifiableSet(sequenceByField.keySet());
  }

}
