package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.QualifiedDataSet;
import org.magenta.annotations.InjectDataSet;

import com.google.common.base.Supplier;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DataSetFieldHandler implements FieldInjectionHandler {

  @Override
  public boolean handle(Field f, Object target, Supplier<DataDomain> current) {

    if (f.isAnnotationPresent(org.magenta.annotations.InjectDataSet.class)) {

      InjectDataSet annotation = f.getAnnotation(org.magenta.annotations.InjectDataSet.class);

      if (DataSet.class.equals(f.getType()) || QualifiedDataSet.class.equals(f.getType())) {
        DataKey key = findKey(f,annotation);
        QualifiedDataSet dataset = key.qualify(supplierFor(key, current));
        FieldInjectorUtils.injectInto(target, f, dataset);
        return true;
      }else{
        throw new IllegalStateException("Annotation "+InjectDataSet.class.getName()+" is present on field named "+f.getName()+", but this field type is not a "+DataSet.class.getName());
      }
    } else {
      return false;
    }
  }

  private DataKey<?> findKey(Field f, InjectDataSet annotation) {

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

  private Supplier<?> supplierFor(final DataKey<?> key, final Supplier<DataDomain> current) {

    return new Supplier() {

      @Override
      public Object get() {
        DataDomain domain = current.get();
        if (domain == null) {
          throw new IllegalStateException("No FixtureBuilder currently bound to the provided supplier : " + current);
        }
        return domain.dataset(key);
      }

    };
  }

}
