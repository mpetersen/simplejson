package de.moritzpetersen.simplejson;

import java.io.CharArrayReader;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Converts JSON input into basic Java objects (numbers, booleans, strings, maps, lists).
 */
public class SimpleJson {
  /**
   * Converts the JSON input into basic Java objects.
   *
   * @param str Well-formed JSON string. Must not be null.
   * @throws IllegalStateException if parsing was not successful.
   * @return A Java object representing the JSON string.
   */
  public Object readValue(final String str) {
    final Reader reader =
        new CharArrayReader(requireNonNull(str, "Input must not be null.").toCharArray());
    return readValue(reader);
  }

  /**
   * Converts the JSON input into basic Java objects.
   *
   * @param reader Well-formed JSON input. Must not be null.
   * @throws IllegalStateException if parsing was not successful.
   * @return A Java object representing the JSON input.
   */
  public Object readValue(final Reader reader) {
    final Crawler crawler = new Crawler(requireNonNull(reader, "Input must not be null."));
    return readValue(crawler);
  }

  private Object readValue(final Crawler crawler) {
    final int cp = crawler.skipWhitespace();
    if (cp == -1) {
      throw new IllegalStateException(format("No content at index %d.", crawler.getCurrentIndex()));
    }
    final char ch = (char) cp;
    return switch (ch) {
      case 'n' -> readNull(crawler);
      case 't', 'f' -> readBoolean(crawler);
      case '"' -> readString(crawler);
      case '{' -> readObject(crawler);
      case '[' -> readArray(crawler);
      default -> readNumber(crawler);
    };
  }

  private Object readArray(final Crawler crawler) {
    final Collection<Object> array = new ArrayList<>();
    // Currently the crawler is at start array, so it needs to be moved forward.
    crawler.nextChar();
    while (true) {
      final Object value = readValue(crawler);
      array.add(value);
      // Determine the next token.
      final char ch = (char) crawler.skipWhitespace();
      crawler.nextChar();
      switch (ch) {
        case ']':
          return array;
        case ',':
          break;
        default:
          throw new IllegalStateException(
              format(
                  "Value separator or end array expected at index %d, but found '%s'.",
                  crawler.getCurrentIndex(), ch));
      }
    }
  }

  private Object readObject(final Crawler crawler) {
    final Map<String, Object> object = new HashMap<>();
    // Currently the crawler is at the start object, so it needs to be moved forward.
    crawler.nextChar();
    while (true) {
      // Skip whitespace until the next quotation mark, in order to read the name of the property.
      final char quotationMark = (char) crawler.skipWhitespace();
      if ('"' != quotationMark) {
        throw new IllegalStateException(
            format(
                "Quotation mark expected at index %d, but found '%s'.",
                crawler.getCurrentIndex(), quotationMark));
      }
      final String name = (String) readString(crawler);
      // Skip whitespace until the name separator.
      final char nameSeparator = (char) crawler.skipWhitespace();
      if (':' != nameSeparator) {
        throw new IllegalStateException(
            format(
                "Name separator expected at index %d, but found '%s'.",
                crawler.getCurrentIndex(), nameSeparator));
      }
      // Crawler is still at name separator, so it needs to be moved forward.
      crawler.nextChar();
      final Object value = readValue(crawler);
      object.put(name, value);
      // Skip whitespace and determine the next token.
      final char ch = (char) crawler.skipWhitespace();
      crawler.nextChar();
      switch (ch) {
        case '}':
          return object;
        case ',':
          break;
        default:
          throw new IllegalStateException(
              format(
                  "Value separator or end object expected at index %d, but found '%s'.",
                  crawler.getCurrentIndex(), ch));
      }
    }
  }

  private Object readNumber(final Crawler crawler) {
    final int index = crawler.getCurrentIndex();
    final String value = crawler.readNumeric();
    try {
      return NumberFormat.getNumberInstance().parse(value);
    } catch (ParseException e) {
      throw new IllegalStateException(format("Illegal value '%s' at index %d.", value, index), e);
    }
  }

  private Object readString(final Crawler crawler) {
    crawler.nextChar();
    final String value = crawler.readUntil('"');
    crawler.nextChar();
    return value;
  }

  private Object readBoolean(final Crawler crawler) {
    return readAlphabetic(crawler, (index, value) -> switch (value) {
      case "true" -> Boolean.TRUE;
      case "false" -> Boolean.FALSE;
      default -> throw new IllegalStateException(format("Illegal value '%s' at index %d.", value, index));
    });
  }

  private Object readNull(final Crawler crawler) {
    return readAlphabetic(crawler, (index, value) -> {
      if (value.equals("null")) {
        return null;
      }
      throw new IllegalStateException(format("Illegal value '%s' at index %d.", value, index));
    });
  }

  private Object readAlphabetic(final Crawler crawler, final BiFunction<Integer, String, Object> fn) {
    final int index = crawler.getCurrentIndex();
    final String value = crawler.readAlphabetic();
    return fn.apply(index, value);
  }
}
