package org.magenta.core.injection;

import java.lang.reflect.Field;

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

        FieldInjectorUtils.injectInto(target, f, supplierOfDataSpecification(current));
        return true;
      }else{
        throw new IllegalStateException("Annotation "+InjectRandomBuilder.class.getName()+" is present on field named "+f.getName()+", but this field type is not a "+RandomBuilder.class.getName());
      }
    } else {
      return false;
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
