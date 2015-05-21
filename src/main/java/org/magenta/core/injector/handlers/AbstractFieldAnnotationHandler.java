package org.magenta.core.injector.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.magenta.core.injector.FieldsExtractor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public abstract class AbstractFieldAnnotationHandler<A extends Annotation> {

  private final FieldsExtractor fieldExtractors;

  public AbstractFieldAnnotationHandler(FieldsExtractor fieldExtractors){
    this.fieldExtractors = fieldExtractors;
  }

  protected Iterable<FieldAnnotation<A>> matchingFields(Object target){
    List<Field> allFields = fieldExtractors.extractAll(target.getClass());

    return FluentIterable.from(allFields).filter(havingAnnotation()).transform(toFieldAnnotationPair());
  }

  private Predicate<Field> havingAnnotation() {
    return new Predicate<Field>(){

      @Override
      public boolean apply(Field f) {
        return f.isAnnotationPresent(getAnnotationType());
      }

    };
  }

  private Function<Field, FieldAnnotation<A>> toFieldAnnotationPair() {
    return new Function<Field,FieldAnnotation<A>>(){

      @Override
      public FieldAnnotation<A> apply(Field input) {
        return new FieldAnnotation<A>(input, input.getAnnotation(getAnnotationType()));
      }

    };
  }

  protected abstract Class<A> getAnnotationType();

  protected static class FieldAnnotation<A extends Annotation> {
    private final Field field;
    private final A annotation;

    public FieldAnnotation(Field field, A annotation) {
      super();
      this.field = field;
      this.annotation = annotation;
    }
    /**
     * @return the field
     */
    public Field getField() {
      return field;
    }
    /**
     * @return the annotation
     */
    public A getAnnotation() {
      return annotation;
    }


  }
}
