package com.codefactory.challenge.UrlShortenerApp.serviceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.codefactory.challenge.UrlShortenerApp.serviceImpl.UrlShortenerServiceImpl.INVALID_URL;
import static com.codefactory.challenge.UrlShortenerApp.serviceImpl.UrlShortenerServiceImpl.SHORT_URL_CANNOT_BE_EMPTY;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    @InjectMocks
    private UrlShortenerServiceImpl serviceImpl;

    private static final String VALID_LONG_URL = "https://www.test.com";
    private static final String VALID_SHORT_URL = "25gE8Y";

    private static Stream<Arguments> invalidLongUrlData() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("testURL"),
                Arguments.of(" "),
                Arguments.of("www.test.com")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidLongUrlData")
    void testGetShortUrlWhenInvalidUrlThenThrowException(String url) {
        Exception exception = assertThrows(Exception.class,
                () -> serviceImpl.getShortUrl(url));
        assertEquals(INVALID_URL, exception.getMessage());
    }

    @Test
    void testGetShortUrlWhenValidUrlThenGeneratesShortUrl() throws Exception {
        String shortUrl = serviceImpl.getShortUrl(VALID_LONG_URL);
        assertNotNull(shortUrl);
    }

    @Test
    void testGetLongUrlWhenEmptyShortUrlThenThrowException() {
        Exception exception = assertThrows(Exception.class,
                () -> serviceImpl.getLongUrl(""));
        assertEquals(SHORT_URL_CANNOT_BE_EMPTY, exception.getMessage());
    }

    private static Stream<Arguments> invalidShortUrlData() {
        return Stream.of(
                Arguments.of("abc"),
                Arguments.of("abcdefg"),
                Arguments.of("abc.efg"),
                Arguments.of("abc efg")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidShortUrlData")
    void testGetLongUrlWhenSHortUrlIncorrectSizeShortUrlThenThrowException(String shortUrl) {
        Exception exception = assertThrows(Exception.class,
                () -> serviceImpl.getLongUrl(shortUrl));
        assertEquals(INVALID_URL, exception.getMessage());
    }

    @Test
    void testValidateEncodedAndDecodedValuesMatch() throws Exception {
        String shortUrl = serviceImpl.getShortUrl(VALID_LONG_URL);
        String longUrl = serviceImpl.getLongUrl(shortUrl);
        assertEquals(VALID_LONG_URL, longUrl);
    }

}