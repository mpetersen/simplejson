package de.moritzpetersen.simplejson;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CrawlerTest {
  @Test
  void verifyReaderIOException() throws Exception {
    try (final Reader reader = mock(Reader.class)) {
      when(reader.read()).thenThrow(IOException.class);

      assertThrows(
          IllegalStateException.class,
          () -> {
            Crawler crawler = new Crawler(reader);
            crawler.nextChar();
          });
    }
  }
}