package org.magenta.core.sequence;

import java.lang.reflect.Field;
import java.util.List;

import org.magenta.Sequence;
import org.magenta.core.injector.extractors.HiearchicalFieldsExtractor;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class SupplierSequenceAdapter<D> implements Sequence<D>{

  private Supplier<D> generator;
  private Supplier<Integer> sizeComputer;

  public SupplierSequenceAdapter(Supplier<D> generator) {
    this.generator = generator;
    //The size of the sequences inside a generator is immutable therefore we can memoize it
    this.sizeComputer = Suppliers.memoize(computeSize(generator));
  }

  @Override
  public D get() {
    return generator.get();
  }

  @Override
  public int size() {
    return this.sizeComputer.get();
  }

  //This is not elegant, too much responsibility put in this class.
  private Supplier<Integer> computeSize(final Supplier<?> generator) {

    return new Supplier<Integer>() {
      @Override
      public Integer get() {
        List<Field> fields = HiearchicalFieldsExtractor.SINGLETON.extractAll(generator.getClass());
        int total = 1;
        for (Field f : fields) {
          if (Sequence.class.isAssignableFrom(f.getType())) {

            try {
              f.setAccessible(true);
              Sequence<?> s = (Sequence) f.get(generator);
              total = total * s.size();
            } catch (Exception e) {
              // error a this point means that there is a problem with the fixture factory configuration, the error will
              // be better reported when an attempt to generate the data will be made.
            }

          }
        }

        return total;
      }

    };

  }

  @Override
  public String toString(){
    return "[adapter to sequence from supplier "+generator.toString()+"]";
  }

}
