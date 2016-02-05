package org.magenta.core.sequence;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public class SequenceCoordinator {


  private Map<CoordinatedSequence<?>,AtomicInteger> indexMap;

  private Set<CoordinatedSequence<?>> currentLoop;

  public SequenceCoordinator(){
    this.indexMap = Maps.newLinkedHashMap();
    this.currentLoop = Sets.newHashSet();
  }

  public void coordinate(CoordinatedSequence<?> sequence){
    this.indexMap.put(sequence,new AtomicInteger(0));
  }

  public int getIndexFor(CoordinatedSequence<?> sequence){

    Preconditions.checkArgument(indexMap.containsKey(sequence), "The sequence %s is not coordinated by this SequenceCoordinator",sequence);

    if(currentLoop.size()==indexMap.size()){
      currentLoop.clear();
      for(Map.Entry<CoordinatedSequence<?>,AtomicInteger> entry:indexMap.entrySet()){
        int newValue = entry.getValue().incrementAndGet();
        if(newValue>=entry.getKey().size()){
          entry.getValue().set(0);
        }else{
          break;
        }
      }
    }

     currentLoop.add(sequence);

     AtomicInteger current = indexMap.get(sequence);
     int toReturn = current.intValue();



     return toReturn;
  }

  public Integer numberOfCombination() {
    int r = 1;
    for (CoordinatedSequence<?> s : indexMap.keySet()) {
      r = r * s.size();
    }
    return r;
  }
}
