package org.magenta.core;

import org.magenta.CycleDetectedInGenerationException;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.GenerationStrategy;
import org.magenta.events.PostDataSetGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;

public class DataSetRelationLoader {

  private static final Logger LOG = LoggerFactory.getLogger(DataSetRelationLoader.class);

  @Subscribe
  public void onGenerationEvent(PostDataSetGenerated event){

    Fixture<? extends DataSpecification> fixture= event.getFixture();
    DataKey<?> generatedDataSetKey = event.getKey();

  //cache the computation of key to load and use a guava event to know when to flush the cache
    for (DataKey k : fixture.strategyKeys()) {
      if (!k.equals(generatedDataSetKey)) {
        GenerationStrategy s = fixture.strategy(k);
        if (Iterables.contains(s.getModifiedDataSet(), generatedDataSetKey)) {
          try {
            LOG.trace("DataSet [{}] is modified by dataset [{}], tiggering generation...",generatedDataSetKey, k);
            fixture.dataset(k).toTransient().get();

            //The dataset identified by key must be persisted again
            DataSet modified = fixture.dataset(generatedDataSetKey);
            if(modified.isPersistent()){
              fixture.dataset(generatedDataSetKey).persist();
            }
          } catch (CycleDetectedInGenerationException cdge) {
            // ignore
            // the key is already being loaded elsewhere
            // cdge.printStackTrace();
          } catch(Exception e){
            LOG.error("Error while loading relation {} of {} : {}",new Object[]{k,generatedDataSetKey,e});
            throw e;
          }
        }
      }
    }

  }
}
