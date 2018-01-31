package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.annotations.InjectFixture;

import com.google.common.reflect.Reflection;

public class FixtureFieldHandler<S extends DataSpecification> implements FieldInjectionHandler<S> {

  @Override
  public boolean handle(Field f, Object target, Supplier<Fixture<S>> context) {
    if (f.isAnnotationPresent(org.magenta.annotations.InjectFixture.class)) {

      InjectFixture annotation = f.getAnnotation(org.magenta.annotations.InjectFixture.class);

      if (Fixture.class.equals(f.getType())) {
        Fixture proxy = Reflection.newProxy(Fixture.class, handler(context));
        FieldInjectorUtils.injectInto(target, f, proxy);
        return true;
      } else {
        throw new IllegalStateException("Annotation " + InjectFixture.class.getName() + " is present on field named " + f.getName()
            + ", but this field type is not a " + Fixture.class.getName());
      }
    } else {
      return false;
    }
  }

  private InvocationHandler handler(final Supplier<Fixture<S>> context) {
    return new InvocationHandler() {

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(context, args);
      }

    };
  }

}
