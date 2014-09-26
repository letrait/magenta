package org.magenta.core.injection;

import java.lang.reflect.Field;

import org.magenta.Fixture;

import com.google.common.base.Supplier;

/**
 * A FieldHandler is a link in the chain of responsibility handled by the FieldProcessor.
 * @author ngagnon
 *
 */
public interface FieldInjectionHandler {

  /**
   * Initialize a field according to its attribute such as annotations.
   *
   * @param f the field to initialize
   * @param target the target object for which we need to set this field
   * @param context a supplier of fixture
   * @return true if the field was handled
   */
  public boolean handle(Field f, Object target, Supplier<Fixture> context);
}
