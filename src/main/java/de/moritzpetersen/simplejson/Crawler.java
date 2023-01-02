package de.moritzpetersen.simplejson;

import java.io.IOException;
import java.io.Reader;
import java.util.function.IntFunction;

import static java.util.Objects.requireNonNull;

class Crawler {
  private final Reader reader;
  private int currentChar;
  private int currentIndex = -1;

  Crawler(final Reader reader) {
    this.reader = requireNonNull(reader, "Reader must not be null.");
    nextChar();
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void nextChar() {
    try {
      currentIndex++;
      currentChar = reader.read();
    } catch (final IOException e) {
      throw new IllegalStateException(String.format("Error while reading from input at index %d.", currentIndex), e);
    }
  }

  public int skipWhitespace() {
    while (currentChar != -1 && Character.isWhitespace(currentChar)) {
      nextChar();
    }
    return currentChar;
  }

  public String readAlphabetic() {
    return read(Character::isAlphabetic);
  }

  public String readUntil(final char delim) {
    return read(cp -> delim != (char) cp);
  }

  public String readNumeric() {
    return read(this::isNumeric);
  }

  private String read(final IntFunction<Boolean> fn) {
    final StringBuilder sb = new StringBuilder();
    while (currentChar != -1 && fn.apply(currentChar)) {
      sb.append((char) currentChar);
      nextChar();
    }
    return sb.toString();
  }

  private boolean isNumeric(final int cp) {
    char ch = (char) cp;
    return switch (ch) {
      case 'e', 'E', '-', '+', '.' -> true;
      default -> Character.isDigit(ch);
    };
  }
}
