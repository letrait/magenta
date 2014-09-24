package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.magenta.DataDomain;
import org.magenta.DataSpecification;
import org.magenta.annotations.InjectDataSpecification;
import org.magenta.annotations.InjectRandomBuilder;
import org.magenta.random.RandomBuilder;

import com.google.common.base.Supplier;

public class DataSpecificationFieldHandler implements FieldInjectionHandler {

  @Override
  public boolean handle(Field f, Object target, Supplier<DataDomain> current) {

    if (f.isAnnotationPresent(org.magenta.annotations.InjectDataSpecification.class)) {

      InjectDataSpecification annotation = f.getAnnotation(org.magenta.annotations.InjectDataSpecification.class);

      if (Supplier.class.equals(f.getType())) {

        assertIsASupplierOfDataSpecification(f);

        FieldInjectorUtils.injectInto(target, f, supplierOfDataSpecification(current));
        return true;
      }else{
        throw new IllegalStateException("Annotation "+InjectRandomBuilder.class.getName()+" is present on field named "+f.getName()+", but this field type is not a "+RandomBuilder.class.getName());
      }
    } else {
      return false;
    }
  }

  private Class<? extends DataSpecification> assertIsASupplierOfDataSpecification(Field f) {
    Type gt = f.getGenericType();
    if (gt instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) gt;
      Type t = pt.getActualTypeArguments()[0];
      if (t instanceof Class) {
        Class keyType = ((Class) t);
        if(!DataSpecification.class.isAssignableFrom(keyType)){
          throw new IllegalStateException("Illegal attempt to inject a DataSpecification into a supplier which is not a DataSpecification supplier.");
        }
        return keyType;
      } else {
        throw new IllegalStateException("DataSpecification cannot be injected into field [" + f.getName() + "] of [" + f.getDeclaringClass()
            .getName() + "] because the specified Supplier is a generic type [" + t
            + "].  A specific type should be declared such as Supplier<DataSpecification>  instead of Supplier<D>.");
      }
    } else {
      throw new IllegalStateException(
          "DataSpecification cannot be injected into field ["
              + f.getName()
              + "] of ["
              + f.getDeclaringClass()
                  .getName()
              + "] because the DataSpecification cannot be derived from the Supplier since it is a rawtype.  A specific type should be declared such as Supplier<DataSet> instead of just Supplier.");
    }

  }

  private Supplier<DataSpecification> supplierOfDataSpecification(final Supplier<DataDomain> current) {
    return new Supplier<DataSpecification>(){

      @Override
      public DataSpecification get() {
        return current.get().getSpecification();
      }
    };
  }

}
