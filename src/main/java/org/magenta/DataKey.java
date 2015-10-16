package org.magenta;

import com.google.common.reflect.TypeToken;

public class DataKey<D> {
public final static String DEFAULT = "default";
  private final TypeToken<D> type;
  private final String qualifier;
  private final boolean generalizable ;

  private  DataKey(TypeToken<D> type, String qualifier, boolean generalizable){
    this.type = type;
    this.qualifier = qualifier;
    this.generalizable = generalizable;
  }

  public static <D> DataKey<D> of(Class<D> type){
    return new DataKey<D>(TypeToken.of(type),DEFAULT, false);
  }

  public static <D> DataKey<D> of(String qualifier, Class<D> type){
    return new DataKey<D>(TypeToken.of(type), qualifier, false);
  }

  public static <D> DataKey<D> of(TypeToken<D> type){
    return new DataKey<D>(type,DEFAULT, false);
  }

  public static <D> DataKey<D> of(String qualifier, TypeToken<D> type){
    return new DataKey<D>(type,qualifier, false);
  }

  public static <D> DataKey<D> of(String qualifier, TypeToken<D> type, boolean generalizable){
    return new DataKey<D>(type,qualifier, true);
  }

  public TypeToken<D> getType() {
   return type;
  }

  public String getQualifier(){
    return qualifier;
  }

  public DataKey<D> generalize() {
   return generalizable ? DataKey.of(type) : this;
  }

  public boolean isGeneralized() {
    return DEFAULT.equals(getQualifier());
  }

  public boolean isGeneralizable() {
    return generalizable;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DataKey)) {
      return false;
    }
    DataKey other = (DataKey) obj;
    if (qualifier == null) {
      if (other.qualifier != null) {
        return false;
      }
    } else if (!qualifier.equals(other.qualifier)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(getType()).append(':').append(getQualifier());
    return builder.toString();
  }









}
