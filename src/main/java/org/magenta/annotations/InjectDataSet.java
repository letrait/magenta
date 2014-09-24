package org.magenta.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.magenta.DataKey;


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface InjectDataSet {
  /**
   * An array of data key.
   * @return
   */
  String value() default DataKey.DEFAULT_QUALIFIER;
  boolean modified() default false;

}
