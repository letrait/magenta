package org.magenta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.magenta.DataKey;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface InjectSequence {
  String value() default DataKey.DEFAULT;

  boolean unique() default false;
}
