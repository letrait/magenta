package org.magenta.testing.domain.playing.cards;

public class Card{

  private Kind kind;

  private Integer value;

  public Kind getKind() {
    return kind;
  }

  public void setKind(Kind kind) {
    this.kind = kind;
  }

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Card [kind=" + kind + ", value=" + value + "]";
  }


}
