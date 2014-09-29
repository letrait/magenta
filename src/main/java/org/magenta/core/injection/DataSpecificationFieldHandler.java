package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.annotations.InjectDataSpecification;

import com.google.common.base.Supplier;

public class DataSpecificationFieldHandler<S extends DataSpecification> implements FieldInjectionHandler<S> {

  @Override
  public boolean handle(Field f, Object target, Supplier<Fixture<S>> current) {

    if (f.isAnnotationPresent(org.magenta.annotations.InjectDataSpecification.class)) {

      InjectDataSpecification annotation = f.getAnnotation(org.magenta.annotations.InjectDataSpecification.class);

      if (DataSpecification.class.isAssignableFrom(f.getType())) {

        Enhancer e = new Enhancer();
        e.setSuperclass(current.get().getSpecification().getClass());
        e.setCallback(interceptor(supplierOfDataSpecification(current)));
        e.setInterceptDuringConstruction(false);

        DataSpecification proxy = (DataSpecification)e.create();


        FieldInjectorUtils.injectInto(target, f, proxy);
        return true;
      }else{
        throw new IllegalStateException("Invalid field : Annotation ["+InjectDataSpecification.class.getName()+"] is present on field named ["+f.getName()+"] on the class ["+f.getDeclaringClass().getCanonicalName()+"],\n but this field type should implement ["+DataSpecification.class.getName()+"] instead of ["+f.getType()+"].");
      }
    } else {
      return false;
    }
  }

  private Callback interceptor(final Supplier<S> current) {
    return new MethodInterceptor() {

      @Override
      public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        return method.invoke(current.get(), args);
      }
    };
  }


  private Supplier<S> supplierOfDataSpecification(final Supplier<Fixture<S>> current) {
    return new Supplier<S>(){

      @Override
      public S get() {
        return current.get().getSpecification();
      }
    };
  }

}
