package org.magenta.core;

import org.magenta.CycleDetectedInGenerationException;
import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;
import org.magenta.GenerationStrategy;
import org.magenta.events.DataSetGenerated;

import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;

public class DataSetRelationLoader {

  @Subscribe
  public void onGenerationEvent(DataSetGenerated event){

    DataDomain<? extends DataSpecification> fixture= event.getFixture();
    DataKey<?> key = event.getKey();

  //cache the computation of key to load and use a guava event to know when to flush the cache
    for (DataKey k : fixture.strategyKeys()) {
      if (!k.equals(key)) {
        GenerationStrategy s = fixture.strategy(k);
        if (Iterables.contains(s.getModifiedDataSet(), key)) {
          try {
            fixture.dataset(k).get();
          } catch (CycleDetectedInGenerationException cdge) {
            // ignore
            // the key is already being loaded elsewhere
            // cdge.printStackTrace();
          }
        }
      }
    }

  }
}
