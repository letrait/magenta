package org.magenta.events;

import org.magenta.Fixture;
import org.magenta.DataKey;

public class DataSetRegistered {

  private final DataKey<?> key;
  private final Fixture<?> fixture;
  private final boolean generated;
  private final boolean persisted;

  public DataSetRegistered(DataKey<?> key, Fixture<?> fixture, boolean generated, boolean persisted) {
    super();
    this.key = key;
    this.fixture = fixture;
    this.generated = generated;
    this.persisted = persisted;
  }

  public DataKey<?> getKey() {
    return key;
  }

  public Fixture<?> getFixture() {
    return fixture;
  }

  public boolean isGenerated() {
    return generated;
  }

  public boolean isPersisted() {
    return persisted;
  }



}
