package org.magenta.events;

import org.magenta.DataDomain;
import org.magenta.DataKey;
import org.magenta.DataSpecification;

public class DataSetGenerated {

  private DataKey<?> key;
  private DataDomain<? extends DataSpecification> fixture;

  public DataSetGenerated(DataKey<?> key,DataDomain<? extends DataSpecification> fixture) {
    super();
    this.key = key;
    this.fixture = fixture;
  }

  public DataKey<?> getKey() {
    return key;
  }

  public DataDomain<? extends DataSpecification> getFixture() {
    return fixture;
  }



}
