package org.magenta.events;

import org.assertj.core.util.Preconditions;
import org.magenta.DataKey;
import org.magenta.Fixture;

public class DataSetFound {

  private final DataKey<?> key;
  private final Fixture fixture;

  private DataSetFound( DataKey<?> key, Fixture fixture){
    this.key = Preconditions.checkNotNull(key);
    this.fixture =  Preconditions.checkNotNull(fixture);
  }

  public static DataSetFound from( DataKey<?> key, Fixture fixture){
    return new DataSetFound(key, fixture);
  }

  public DataKey<?> getKey() {
    return key;
  }

  public Fixture getFixture() {
    return fixture;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fixture == null) ? 0 : fixture.hashCode());
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DataSetFound other = (DataSetFound) obj;
    if (!fixture.equals(other.fixture))
      return false;
    if (!key.equals(other.key))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DataSetFound event for ["+key+"] in fixture[" + fixture + "]";
  }






}
