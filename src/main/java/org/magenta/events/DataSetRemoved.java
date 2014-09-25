package org.magenta.events;

import org.magenta.DataDomain;
import org.magenta.DataKey;

public class DataSetRemoved {

  private final DataKey<?> key;
  private final DataDomain<?> fixture;

  public DataSetRemoved(DataKey<?> key, DataDomain<?> fixture) {
    super();
    this.key = key;
    this.fixture = fixture;
  }

  public DataKey<?> getKey() {
    return key;
  }

  public DataDomain<?> getFixture() {
    return fixture;
  }





}
