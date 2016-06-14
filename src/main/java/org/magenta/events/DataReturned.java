package org.magenta.events;

public class DataReturned {

  private final Object data;
  private final int index;

  private DataReturned(Object data,  int index){
    this.data = data;
    this.index = index;
  }

  public static DataReturned of(Object data, int index){
    return new DataReturned(data, index);
  }

  public Object getData(){
    return data;
  }

  public int getIndex(){
    return index;
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((data == null) ? 0 : data.hashCode());
    result = prime * result + index;
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
    DataReturned other = (DataReturned) obj;
    if (!data.equals(other.data))
      return false;
    if (index != other.index)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DataReturned event for ["+data+"] at index [" + index + "]";
  }




}
