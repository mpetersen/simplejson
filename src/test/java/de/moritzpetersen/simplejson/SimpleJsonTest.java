package de.moritzpetersen.simplejson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class SimpleJsonTest {
  private SimpleJson simpleJson;

  @BeforeEach
  void init() {
    simpleJson = new SimpleJson();
  }

  @Test
  void verifyNull() {
    assertException(NullPointerException.class, () -> simpleJson.readValue((String) null));
    assertException(NullPointerException.class, () -> simpleJson.readValue((Reader) null));
  }

  @Test
  void verifyEmpty() {
    assertIllegalStateException(() -> simpleJson.readValue(""));

    assertIllegalStateException(() -> simpleJson.readValue("    "));
  }

  @Test
  void verifyNullValue() {
    assertNull(simpleJson.readValue("null"));
    assertNull(simpleJson.readValue("   null  "));

    assertIllegalStateException(() -> simpleJson.readValue("not null"));
    assertIllegalStateException(() -> simpleJson.readValue("NULL"));
  }

  @Test
  void verifyBoolean() {
    assertTrue((boolean) simpleJson.readValue("true"));
    assertTrue((boolean) simpleJson.readValue("  true  "));
    assertFalse((boolean) simpleJson.readValue("false"));
    assertFalse((boolean) simpleJson.readValue("  false  "));

    assertIllegalStateException(() -> simpleJson.readValue("falsetrue"));
    assertIllegalStateException(() -> simpleJson.readValue("FALSE"));
    assertIllegalStateException(() -> simpleJson.readValue("TRUE"));
  }

  @Test
  void verifyString() {
    assertEquals("Hello, World!", simpleJson.readValue("\"Hello, World!\""));
    assertEquals("Hello, World!", simpleJson.readValue("  \"Hello, World!\"  "));
  }

  @Test
  void verifyNumber() {
    assertEquals(123L, simpleJson.readValue("123"));
    assertEquals(123.456D, simpleJson.readValue("123.456"));
    assertEquals(123456L, simpleJson.readValue("123.456E3"));
    assertEquals(0.123456D, simpleJson.readValue("123.456E-3"));
  }

  @Test
  void verifyObject() {
    assertMap("{\"name\":\"Moritz\"}", 1, "name", "Moritz");
    assertMap("  {  \"name\" :   \"Moritz\"  } ", 1, "name", "Moritz");
    assertMap("{\"name\":\"Moritz\",\"answer\":42}", 2, "name", "Moritz", "answer", 42L);

    Map<String, Object> actual =
        assertMap(
            "{\"name\":\"Moritz\",\"data\":{\"foo\":\"bar\"},\"answer\":42}",
            3,
            "name",
            "Moritz",
            "answer",
            42L);
    Map<String, Object> data = (Map<String, Object>) actual.get("data");
    assertEquals("bar", data.get("foo"));

    assertIllegalStateException(() -> simpleJson.readValue("{\"name\":\"Moritz\":"));

    assertIllegalStateException(() -> simpleJson.readValue("{\"name\"}"));

    assertIllegalStateException(() -> simpleJson.readValue("{name:Moritz}"));
  }

  private Map<String, Object> assertMap(
      final String jsonInput, final int expectedEntries, final Object... expectedData) {
    final Map<String, Object> expected = new HashMap<>();
    for (int i = 0; i < expectedData.length; ) {
      String key = (String) expectedData[i++];
      Object value = expectedData[i++];
      expected.put(key, value);
    }
    final Map<String, Object> actual = (Map<String, Object>) simpleJson.readValue(jsonInput);
    assertEquals(expectedEntries, actual.size());
    expected.forEach((key, value) -> assertEquals(value, actual.get(key)));
    return actual;
  }

  @Test
  void verifyArray() {
    assertList("[true,\"Moritz\",-123.45]", true, "Moritz", -123.45D);
    assertList(
        "[true,\"Moritz\",-123.45, {\"foo\":\"bar\"}]",
        true,
        "Moritz",
        -123.45D,
        Map.of("foo", "bar"));
    Map<String, Object> map =
        (Map<String, Object>) simpleJson.readValue("{\"colors\":[\"red\",\"blue\"],\"answer\":42}");
    assertEquals(Arrays.asList("red", "blue"), map.get("colors"));
    assertEquals(42L, map.get("answer"));

    assertIllegalStateException(() -> simpleJson.readValue("[true:"));
  }

  private void assertList(final String jsonInput, final Object... expectedData) {
    final List<Object> expected = Arrays.asList(expectedData);
    final Object actual = simpleJson.readValue(jsonInput);

    assertEquals(expected, actual);
  }

  private void assertIllegalStateException(final Executable executable) {
    assertException(IllegalStateException.class, executable);
  }

  private <T extends Throwable> void assertException(
      final Class<T> exceptionClass, final Executable executable) {
    final T ex = assertThrows(exceptionClass, executable);
    System.out.println("Expected exception: " + ex.getMessage());
  }
}
