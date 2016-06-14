package org.magenta.events;

import org.magenta.DataKey;
import org.magenta.Fixture;

public class DataGenerated {

  private final DataKey key;
  private final Object data;
  private final Fixture fixture;

  private <D> DataGenerated(DataKey<?> key, D data,  Fixture fixture){
    this.key= key;
    this.data = data;
    this.fixture = fixture;
  }

  public static <D> DataGenerated of(DataKey<?> key, D data, Fixture fixture){
    return new DataGenerated(key, data, fixture);
  }

  public Object getData(){
    return data;
  }

  public Fixture getFixture(){
    return fixture;
  }

  public DataKey getKey(){
    return key;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((data == null) ? 0 : data.hashCode());
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
    DataGenerated other = (DataGenerated) obj;
    if (!data.equals(other.data))
      return false;
    return true;
  }

  @Override
  public String toString() {


    return "DataGenerated event for ["+key+"] in fixture[" + fixture + "]";
  }



}
