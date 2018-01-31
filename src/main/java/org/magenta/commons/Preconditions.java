package org.magenta.commons;

public class Preconditions {

  public static <C> C checkNotNull(C reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }

  public static void checkState(boolean expression, String errorMessage) {
    if (!expression) {
      throw new IllegalStateException(String.valueOf(errorMessage));
    }

  }

  public static void checkState(
      boolean expression,
       String errorMessageTemplate,
       Object... errorMessageArgs) {
    if (!expression) {
      throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
    }
  }

  public static void checkArgument(boolean expression, String errorMessage) {

    if (!expression) {
      throw new IllegalArgumentException(errorMessage);
    }
  }

  public static void checkArgument(
      boolean expression,
       String errorMessageTemplate,
       Object... errorMessageArgs) {
    if (!expression) {
      throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
    }
  }

  static String format(String template, Object... args) {
    template = String.valueOf(template); // null -> "null"

    args = args == null ? new Object[] {"(Object[])null"} : args;

    // start substituting the arguments into the '%s' placeholders
    StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
    int templateStart = 0;
    int i = 0;
    while (i < args.length) {
      int placeholderStart = template.indexOf("%s", templateStart);
      if (placeholderStart == -1) {
        break;
      }
      builder.append(template, templateStart, placeholderStart);
      builder.append(args[i++]);
      templateStart = placeholderStart + 2;
    }
    builder.append(template, templateStart, template.length());

    // if we run out of placeholders, append the extra args in square braces
    if (i < args.length) {
      builder.append(" [");
      builder.append(args[i++]);
      while (i < args.length) {
        builder.append(", ");
        builder.append(args[i++]);
      }
      builder.append(']');
    }

    return builder.toString();
  }





}
