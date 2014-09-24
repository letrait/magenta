package org.magenta.events;

import org.magenta.DataKey;

public class DataSetRegistered {

  private final DataKey<?> key;
  private final boolean generated;
  private final boolean persisted;

  public DataSetRegistered(DataKey<?> key, boolean generated, boolean persisted) {
    super();
    this.key = key;
    this.generated = generated;
    this.persisted = persisted;
  }


}
