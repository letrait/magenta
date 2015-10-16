package org.magenta.random;

import com.google.common.collect.Range;

/**
 *  Helper class that generates random {@code  strings}.
 *
 * @author ngagnon
 *
 */
public class RandomString {

  private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras "
      + "convallis vitae magna ut elementum. Nam lobortis placerat ultrices. Nullam auctor est sit amet "
      + "nulla commodo, nec posuere sem placerat. Vestibulum interdum lobortis leo, in dictum neque "
      + "accumsan eu. Aenean quis turpis rhoncus, imperdiet quam ac, adipiscing magna. Cras interdum "
      + "tempus tortor sit amet volutpat. Aliquam ac diam a sem interdum sollicitudin et quis metus."
      + " Ut ac lorem ipsum. Donec quam sapien, placerat eget nisi ut, varius imperdiet purus."
      + " Pellentesque rhoncus tempus odio, vel egestas felis. Praesent pretium suscipit arcu."
      + " Donec turpis massa, dignissim suscipit diam non, laoreet tempus urna. Phasellus quis interdum"
      + " tellus. Aliquam dictum nec nisi et vulputate. Vestibulum vitae dui vel sapien hendrerit eleifend."
      + " Aenean vel libero varius, facilisis nulla eget, euismod eros. Sed ullamcorper, quam a iaculis"
      + " elementum, dolor turpis aliquam arcu, ac sollicitudin felis felis eget dolor. Vestibulum"
      + " lobortis vestibulum urna et imperdiet. Class aptent taciti sociosqu ad litora torquent per"
      + " conubia nostra, per inceptos himenaeos. Nulla nec suscipit ipsum. Phasellus euismod at mauris"
      + " sed tristique. Nunc pharetra orci at tortor placerat, vitae aliquam erat tristique. Vestibulum"
      + " hendrerit viverra enim. Vestibulum venenatis fringilla lacus tristique ornare. Interdum et"
      + " malesuada fames ac ante ipsum primis in faucibus. Vivamus varius purus arcu. Suspendisse vulputate"
      + " nulla sed egestas ultrices. Fusce pellentesque, elit sed semper fringilla, arcu justo lobortis"
      + " massa, pellentesque tincidunt neque risus a nulla. Ut posuere mauris at sem imperdiet, eu"
      + " consectetur justo fringilla. Pellentesque in imperdiet turpis. Curabitur varius egestas turpis"
      + " id rutrum.  Pellentesque tempor lacus lectus, ut accumsan ipsum volutpat a. Nam vulputate"
      + " tincidunt nisl, a scelerisque augue fringilla vel. Duis tincidunt tellus ac turpis feugiat"
      + " tempor. Suspendisse lacus risus, dictum sed arcu quis, faucibus hendrerit mauris. Vivamus"
      + " venenatis tellus at tristique luctus. Phasellus vitae eros vitae sapien ornare porttitor eu"
      + " in justo. Nulla facilisi. Proin in risus vitae nisi dignissim imperdiet. Morbi eget dui eget"
      + " leo varius sollicitudin fringilla a urna. Nunc venenatis ultrices purus, a cursus lectus volutpat"
      + " ac. Nulla tincidunt justo augue, nec posuere lorem mollis sed. Integer euismod, urna a mollis"
      + " blandit, arcu ipsum rutrum elit, sed suscipit est tellus iaculis quam. Phasellus varius lobortis"
      + " sem eget eleifend.Mauris mattis lorem sed rhoncus gravida. Integer elementum, justo id hendrerit"
      + " fermentum, urna arcu aliquet mi, non varius nibh risus in purus. Ut viverra, elit non porta"
      + " laoreet, erat tortor dignissim tellus, non venenatis erat lorem eu quam. Etiam hendrerit magna"
      + " augue. Suspendisse iaculis scelerisque augue ut pretium. Fusce nec dignissim sapien. Maecenas" + " lacinia luctus ante tincidunt varius.";

  private final String alphabet;
  private final String letters;
  private final String numbers;
  private final RandomInteger integers;

  /**
   * Default constructor.
   *
   * @param alphabet the alphabet from which to generate {@code strings}.
   * @param integers used to select letters from the alphabet.
   */
  public RandomString(String alphabet, RandomInteger integers) {
    this.alphabet = alphabet;
    this.letters = extractLetters(alphabet);
    this.numbers = extractNumbers(alphabet);
    this.integers = integers;
  }

  private String extractNumbers(String alphabet) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < alphabet.length(); i++) {
      char current = alphabet.charAt(i);
      if (Character.isDigit(alphabet.charAt(i))) {
        sb.append(current);
      }
    }
    return sb.toString();
  }

  private String extractLetters(String alphabet) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < alphabet.length(); i++) {
      char current = alphabet.charAt(i);
      if (Character.isLetter(alphabet.charAt(i))) {
        sb.append(current);
      }
    }
    return sb.toString();
  }

  /**
   * Generate a string.
   *
   * @param length
   *          the length of the string to generate
   * @return a randomly generated string
   */
  public String charabia(int length) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(anyChar(letters));
    }
    return sb.toString();
  }

  private char anyChar(String alphabet) {
    return alphabet.charAt(integers.anyPositive(alphabet.length()));
  }

  /**
   * Generate a string using the specified {@code example}. The generated string will have the same length as the {@code example} and
   * will contain letters and numbers at the same place they are found in the {@code example}.
   * <p>
   * Some examples:
   * <ul>
   * <li>{@code 123-456-7890} will produce  strings such as {@code 333-6127-3357, 324-7578-7412, 895-467-1654}</li>
   * <li>{@code A1A 1A1 } will produce  strings such as {@code H2G 5M2, K4F 8Q2, G9X 4X4}</li>
   * <li>{@code CH4NG3_M3 } will produce  strings such as {@code ES8AI8_J1, JQ4JF7_K4}</li>
   * </ul>
   *
   *
   * @param example the example
   * @return a generated string
   */
  public String generateFromExample(String example) {
    StringBuilder sb = new StringBuilder();
    boolean escapeMode = false;
    for (int i = 0; i < example.length(); i++) {
      char current = example.charAt(i);
      if (!escapeMode && Character.isLetter(current)) {
        sb.append(anyChar(letters));
      } else if (!escapeMode && Character.isDigit(current)) {
        sb.append(anyChar(numbers));
      } else if (!escapeMode && current == '$') {
        escapeMode = true;
      } else {
        sb.append(current);
        escapeMode = false;
      }
    }
    return sb.toString();
  }

  /**
   * Generate randomly a string.
   *
   * @param lengthRange
   *          the variable length of the string to generate
   * @return a randomly generated string
   */
  public String charabia(Range<Integer> lengthRange) {
    final StringBuilder sb = new StringBuilder();
    int length = integers.any(lengthRange);
    for (int i = 0; i < length; i++) {
      sb.append(anyChar(letters));
    }
    return sb.toString();
  }

  /**
   * Generate "lorem ipsum" text for the given length.
   *
   * @param length
   *          the length of the text
   * @return a "lorem ipsum" text
   */

  public String loremipsum(int length) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(LOREM_IPSUM.charAt(i));
    }
    return sb.toString();
  }
}
