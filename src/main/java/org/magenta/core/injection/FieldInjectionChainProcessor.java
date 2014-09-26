package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.util.List;

import org.magenta.Fixture;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Process each fields of a class and initialize them using a chain of {@link FieldInjectionHandler}.
 *
 * @author ngagnon
 *
 */
public class FieldInjectionChainProcessor implements Injector {

  @SuppressWarnings("rawtypes")
  private Supplier<Fixture> current;

  private Function<Class<?>,List<Field>> fieldFinder;
  private List<FieldInjectionHandler> handlers;

  public FieldInjectionChainProcessor(@SuppressWarnings("rawtypes") Supplier<Fixture> current, Function<Class<?>, List<Field>> fieldFinder, List<FieldInjectionHandler> handlers){
    this.current = current;
    this.fieldFinder = fieldFinder;
    this.handlers = handlers;
  }

  /* (non-Javadoc)
   * @see org.magenta.core.injection.Injector#inject(java.lang.Object)
   */
  @Override
  public <O> O inject(O o){

    List<Field> fields = fieldFinder.apply(o.getClass());

    for(Field f:fields){
        for(FieldInjectionHandler handler:handlers){
          if(handler.handle(f, o, current)){
            break;
          }
        }

    }
    return o;
  }


}
