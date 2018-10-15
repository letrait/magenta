package org.magenta;

import java.util.Collection;

import org.magenta.core.RestrictableDataSetImpl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;

public interface RestrictableDataSet<D> extends DataSet<D> {

  RestrictableDataSet<D> restrictTo(Object firstArgs, Object...remainings);

}
