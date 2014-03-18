package org.magenta.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.magenta.DataKey;

/**
 * Annotation that identifies a {@link DataKey}.
 *
 * @author ngagnon
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface Key {
  /**
   * The data type associated to this key.
   *
   */
  Class<?> value();

  /**
   * The qualified of this {@link DataKey} or the
   * {@link DataKey#DEFAULT_QUALIFIER} if none specified.
   *
   */
  String qualifier() default DataKey.DEFAULT_QUALIFIER;
}
