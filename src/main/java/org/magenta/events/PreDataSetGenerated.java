package org.magenta.events;

import org.magenta.DataDomain;
import org.magenta.DataKey;

public class PreDataSetGenerated {

  private DataKey<?> key;
  private Iterable<?> data;
  private  DataDomain<?> fixture;

  public PreDataSetGenerated(DataKey<?> key, Iterable<?> data, DataDomain<?> fixture) {
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

  public DataDomain<?> getFixture(){
    return this.fixture;
  }


}
