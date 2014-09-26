package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.magenta.Fixture;
import org.magenta.annotations.InjectFluentRandom;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;

/**
 * Inject the current RandomBuilderImplobject into a given field.
 *
 * @author ngagnon
 *
 */
public class FluentRandomFieldHandler implements FieldInjectionHandler{

  @Override
  public boolean handle(Field f, Object target, Supplier<Fixture> current) {

    if (f.isAnnotationPresent(org.magenta.annotations.InjectFluentRandom.class)) {

      InjectFluentRandom annotation = f.getAnnotation(org.magenta.annotations.InjectFluentRandom.class);

      if (FluentRandom.class.equals(f.getType())) {

        Enhancer e = new Enhancer();
        e.setSuperclass(FluentRandom.class);
        e.setCallback(interceptor(supplierOfFluentRandom(current)));
        e.setInterceptDuringConstruction(false);

        FluentRandom proxy = (FluentRandom)e.create();

        FieldInjectorUtils.injectInto(target, f, proxy);
        return true;
      }else{
        throw new IllegalStateException("Annotation "+InjectFluentRandom.class.getName()+" is present on field named "+f.getName()+", but this field type is not a "+FluentRandom.class.getName());
      }
    } else {
      return false;
    }


  }

  private Callback interceptor(final Supplier<FluentRandom> current) {
    return new MethodInterceptor() {

      @Override
      public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        return method.invoke(current.get(), args);
      }
    };
  }

  private InvocationHandler handler(final Supplier<FluentRandom> current) {
   return new InvocationHandler() {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

      FluentRandom r = current.get();

      return method.invoke(r, args);
    }
  };
  }

  private Supplier<FluentRandom> supplierOfFluentRandom(final Supplier<Fixture> current) {
    return new Supplier<FluentRandom>() {

      @Override
      public FluentRandom get() {
        Fixture domain = current.get();
        if (domain == null) {
          throw new IllegalStateException("No FixtureBuilder currently bound to the provided supplier : " + current);
        }
        return domain.getRandomizer();
      }

    };
  }

}
