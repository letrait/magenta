package org.magenta.events;

import org.magenta.Fixture;
import org.magenta.DataKey;
import org.magenta.DataSpecification;

public class PostDataSetGenerated {

  private DataKey<?> key;
  private Fixture<? extends DataSpecification> fixture;

  public PostDataSetGenerated(DataKey<?> key,Fixture<? extends DataSpecification> fixture) {
    super();
    this.key = key;
    this.fixture = fixture;
  }

  public DataKey<?> getKey() {
    return key;
  }

  public Fixture<? extends DataSpecification> getFixture() {
    return fixture;
  }



}
