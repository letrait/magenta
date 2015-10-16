package org.magenta.core.injector.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSupplier;
import org.magenta.Fixture;
import org.magenta.annotation.InjectDataSet;
import org.magenta.core.DataSetImpl;
import org.magenta.core.DataSetSupplier;
import org.magenta.core.data.supplier.ForwardingDataSupplier;
import org.magenta.core.injector.FieldInjectionHandler;
import org.magenta.core.injector.FieldInjectorUtils;
import org.magenta.core.injector.FieldsExtractor;

import com.google.common.base.Supplier;

public class DataSetFieldHandler extends AbstractFieldAnnotationHandler<InjectDataSet> implements FieldInjectionHandler{


  public DataSetFieldHandler(FieldsExtractor extractors) {
    super(extractors);
  }

  @Override
  protected Class<InjectDataSet> getAnnotationType() {
    return InjectDataSet.class;
  }

  @Override
  public void injectInto(Object target, Supplier<? extends Fixture> fixture) {
    for(FieldAnnotation<InjectDataSet> f:matchingFields(target)){
      if (DataSet.class.equals(f.getField().getType())) {
        injectProxyDataSet(f.getField(), target, fixture, f.getAnnotation());

      }else{
        throw invalidFieldType(f.getField());
      }
    }
  }


  private IllegalStateException invalidFieldType(Field f) {
    return new IllegalStateException("Annotation "+InjectDataSet.class.getName()+" is present on field named "+f.getName()+", but this field type is not a "+DataSet.class.getName());
  }

  private <T> DataKey<T> determinDataKeyFromGenericType(Field f, InjectDataSet annotation) {

    String qualifier = annotation.value();

    Type gt = f.getGenericType();
    if (gt instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) gt;
      Type t = pt.getActualTypeArguments()[0];
      if (t instanceof Class) {
        Class<T> keyType = ((Class<T>) t);

        return DataKey.of(qualifier, keyType);
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
              + "] because the key cannot be derived from the DataSet since it is a rawtype.  A specific type should be declared such as a DataSet<Integer> instead of just a raw DataSet.");
    }

  }

  private void injectProxyDataSet(Field f, Object target, final Supplier<? extends Fixture> fixture, InjectDataSet annotation) {
    final DataKey<?> key = determinDataKeyFromGenericType(f,annotation);

    DataSet<?> dataset = newDataSetProxy(key, fixture);

    FieldInjectorUtils.injectInto(target, f, dataset);

  }

  private <D> DataSet<D> newDataSetProxy(final DataKey<D> key, final Supplier<? extends Fixture> fixture) {
    return new DataSetImpl<D>(newDataSupplierProxy(key, fixture));
  }

  private <D> DataSupplier<D> newDataSupplierProxy(DataKey<D> key, Supplier<? extends Fixture> fixture) {

    return new ForwardingDataSupplier<D>(DataSetSupplier.forKey(key, fixture));

  }












}
