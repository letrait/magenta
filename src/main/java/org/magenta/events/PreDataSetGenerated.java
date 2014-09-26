package org.magenta.events;

import org.magenta.Fixture;
import org.magenta.DataKey;

public class PreDataSetGenerated {

  private DataKey<?> key;
  private Iterable<?> data;
  private  Fixture<?> fixture;

  public PreDataSetGenerated(DataKey<?> key, Iterable<?> data, Fixture<?> fixture) {
    super();
    this.key = key;
    this.data = data;
    this.fixture = fixture;
  }

  public DataKey<?> getKey() {
    return key;
  }
  public Iterable<?> getData() {
    return data;
  }

  public Fixture<?> getFixture(){
    return this.fixture;
  }


}
