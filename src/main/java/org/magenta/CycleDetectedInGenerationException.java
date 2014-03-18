package org.magenta;

public class CycleDetectedInGenerationException extends IllegalStateException {


  private static final long serialVersionUID = 1L;

  public CycleDetectedInGenerationException(String message) {
    super(message);
  }

}
