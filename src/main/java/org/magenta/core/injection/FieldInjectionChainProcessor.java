package org.magenta.core.injection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.magenta.DataSpecification;
import org.magenta.Fixture;


/**
 * Process each fields of a class and initialize them using a chain of {@link FieldInjectionHandler}.
 *
 * @author ngagnon
 *
 */
public class FieldInjectionChainProcessor<S extends DataSpecification> implements Injector {

  private Supplier<Fixture<S>> current;

  private Function<Class<?>,List<Field>> fieldFinder;
  private List<FieldInjectionHandler<S>> handlers;

  public FieldInjectionChainProcessor(Supplier<Fixture<S>> current, Function<Class<?>, List<Field>> fieldFinder, List<FieldInjectionHandler<S>> handlers){
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
        for(FieldInjectionHandler<S> handler:handlers){
          if(handler.handle(f, o, current)){
            break;
          }
        }

    }
    return o;
  }


}
