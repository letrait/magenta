package org.magenta.generators;

import java.util.List;

import org.magenta.DataKey;
import org.magenta.annotations.TriggeredGeneration;
import org.magenta.annotations.Key;

import com.google.common.collect.Lists;

/**
 * Helper class that is able to read {@link TriggeredGeneration} annotation and extract the related {@link DataKey} from them.
 *
 * @author ngagnon
 *
 */
public class GeneratorAnnotationHelper {

  private GeneratorAnnotationHelper() {
    // singleton
  }

  /**
   * Read the annotations in <code>clazz</code> and extract the affected data set keys from them.
   * @param clazz the class to read the annotation from
   * @return the list of affected data set keys.
   */
  public static List<DataKey<?>> getAffectedDataSet(Class<?> clazz) {

    List<DataKey<?>> affecteds = Lists.newArrayList();

    if (clazz.isAnnotationPresent(TriggeredGeneration.class)) {
      TriggeredGeneration annotation = clazz.getAnnotation(TriggeredGeneration.class);

      if (annotation.value().length > 0) {
        for (Key key : annotation.value()) {
          DataKey<?> dsKey = DataKey.makeQualified(key.qualifier(), key.value());
          affecteds.add(dsKey);
        }
      } else {
        // impossible, @AffectedDataSet cannot be empty by definition
        throw new IllegalStateException("AffectedDataSet annotation cannot be empty");
      }
    }else{
      Class parent = clazz.getSuperclass();
      if(parent != null){
        return getAffectedDataSet(parent);
      }
    }

    return affecteds;

  }
}
