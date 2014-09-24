package org.magenta.generators;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.magenta.DataKey;
import org.magenta.annotations.InjectDataSet;
import org.magenta.core.injection.HiearchicalFieldsFinder;

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

    List<Field> fields = HiearchicalFieldsFinder.SINGLETON.apply(clazz);

    for(Field f:fields){
      if(f.isAnnotationPresent(InjectDataSet.class)){
        InjectDataSet annotation = f.getAnnotation(InjectDataSet.class);
        if(annotation.modified()){
          DataKey k = findKey(f, annotation);
          affecteds.add(k);
        }
      }
    }

    return affecteds;

  }

  private static DataKey<?> findKey(Field f, InjectDataSet annotation) {

    String qualifier = annotation.value();

    Type gt = f.getGenericType();
    if (gt instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) gt;
      Type t = pt.getActualTypeArguments()[0];
      if (t instanceof Class) {
        Class keyType = ((Class) t);

        return DataKey.makeQualified(qualifier, keyType);
      } else {
        throw new IllegalStateException("Dataset cannot be injected into field [" + f.getName() + "] of [" + f.getDeclaringClass()
            .getName() + "] because the specified DataSet is a generic type [" + t
            + "].  A specific type should be declared such as DataSet<Integer>  instead of DataSet<D>.");
      }
    } else {
      throw new IllegalStateException(
          "Dataset cannot be injected into field ["
              + f.getName()
              + "] of ["
              + f.getDeclaringClass()
                  .getName()
              + "] because the key cannot be derived from the DataSet since it is a rawtype.  A specific type should be declared such as DataSet<Integer> instead of just DataSet.");
    }

  }
}
