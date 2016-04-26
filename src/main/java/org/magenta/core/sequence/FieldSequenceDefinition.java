package org.magenta.core.sequence;

import java.lang.reflect.Field;

import org.magenta.DataKey;

import com.google.common.base.Preconditions;

public class FieldSequenceDefinition {

  public enum Type {
    ATTRIBUTE, ITERABLE, SEQUENCE, UNIQUE_SEQUENCE, DATASET;
  }

  private final Field field;

  private final DataKey<?> key;

  private final Type type;

  private FieldSequenceDefinition(Field field, DataKey<?> key, Type type) {
    this.field = Preconditions.checkNotNull(field);
    this.key = Preconditions.checkNotNull(key);
    this.type = Preconditions.checkNotNull(type);
  }

  public static  FieldSequenceDefinition make(Field field, DataKey<?> key, Type type) {
    return new FieldSequenceDefinition(field, key, type);
  }

  public Field getField() {
    return field;
  }

  public DataKey<?> getKey() {
    return key;
  }

  public Type getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (field.hashCode());
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + (type.hashCode());
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
    FieldSequenceDefinition other = (FieldSequenceDefinition) obj;
    if (!field.equals(other.field))
      return false;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "FieldSequenceDefinition [field=" + field + ", key=" + key + ", type=" + type + "]";
  }



}
