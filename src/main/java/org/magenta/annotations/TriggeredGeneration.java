package org.magenta.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates the list of data sets being affected by the generator being annotated with this annotation.
 *
 * @author ngagnon
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface TriggeredGeneration {
  /**
   * An array of data key.
   * @return
   */
  Key[] value();
}
