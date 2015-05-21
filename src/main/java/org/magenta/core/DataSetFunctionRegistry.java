package org.magenta.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.Fixture;

import com.google.common.base.Function;

public class DataSetFunctionRegistry {

  private final Map<DataKey<?>,Function<? super Fixture,? extends DataSet<?>>> functionMaps;

  public DataSetFunctionRegistry(){
    this.functionMaps = new LinkedHashMap<>();
  }

  public <D> void register(DataKey<D> key, Function<? super Fixture, DataSet<D>> function) {
    this.functionMaps.put(checkNotNull(key), checkNotNull(function));
  }

  public <D> Function<Fixture,DataSet<D>> get(DataKey<D> key) {

    return (Function<Fixture, DataSet<D>>) this.functionMaps.get(key);
  }

  public Set<DataKey<?>> keys(){
    return functionMaps.keySet();
  }

}
