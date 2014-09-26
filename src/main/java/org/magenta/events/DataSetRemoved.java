package org.magenta.events;

import org.magenta.Fixture;
import org.magenta.DataKey;

public class DataSetRemoved {

  private final DataKey<?> key;
  private final Fixture<?> fixture;

  public DataSetRemoved(DataKey<?> key, Fixture<?> fixture) {
    super();
    this.key = key;
    this.fixture = fixture;
  }

  public DataKey<?> getKey() {
    return key;
  }

  public Fixture<?> getFixture() {
    return fixture;
  }





}
