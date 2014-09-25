package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.magenta.DataDomain;
import org.magenta.annotations.InjectRandomBuilder;
import org.magenta.random.RandomBuilder;

import com.google.common.base.Supplier;
import com.google.common.reflect.Reflection;

/**
 * Inject the current RandomBuilder object into a given field.
 *
 * @author ngagnon
 *
 */
public class RandomBuilderFieldHandler implements FieldInjectionHandler{

  @Override
  public boolean handle(Field f, Object target, Supplier<DataDomain> current) {

    if (f.isAnnotationPresent(org.magenta.annotations.InjectRandomBuilder.class)) {

      InjectRandomBuilder annotation = f.getAnnotation(org.magenta.annotations.InjectRandomBuilder.class);

      if (RandomBuilder.class.equals(f.getType())) {

        RandomBuilder proxy = Reflection.newProxy(RandomBuilder.class, handler(supplierOfRandoms(current)));

        FieldInjectorUtils.injectInto(target, f, proxy);
        return true;
      }else{
        throw new IllegalStateException("Annotation "+InjectRandomBuilder.class.getName()+" is present on field named "+f.getName()+", but this field type is not a "+RandomBuilder.class.getName());
      }
    } else {
      return false;
    }


  }

  private InvocationHandler handler(final Supplier<RandomBuilder> current) {
   return new InvocationHandler() {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

      RandomBuilder r = current.get();

      return method.invoke(r, args);
    }
  };
  }

  private Supplier<RandomBuilder> supplierOfRandoms(final Supplier<DataDomain> current) {
    return new Supplier<RandomBuilder>() {

      @Override
      public RandomBuilder get() {
        DataDomain domain = current.get();
        if (domain == null) {
          throw new IllegalStateException("No FixtureBuilder currently bound to the provided supplier : " + current);
        }
        return domain.getRandomizer();
      }

    };
  }

}
