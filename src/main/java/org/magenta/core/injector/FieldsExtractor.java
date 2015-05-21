package org.magenta.core.injector;

import java.lang.reflect.Field;
import java.util.List;

public interface FieldsExtractor {

  List<Field> extractAll(Class<? extends Object> type);

}
