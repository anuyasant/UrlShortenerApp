package com.codefactory.challenge.UrlShortenerApp.serviceImpl;

import com.codefactory.challenge.UrlShortenerApp.entity.UrlDataEntity;
import com.codefactory.challenge.UrlShortenerApp.exception.UrlShortenerException;
import com.codefactory.challenge.UrlShortenerApp.repository.UrlShortenerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static com.codefactory.challenge.UrlShortenerApp.serviceImpl.UrlShortenerServiceImpl.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    @Mock
    private UrlShortenerRepository urlShortenerRepository;

    @Captor
    private ArgumentCaptor<UrlDataEntity> dataEntityArgumentCaptor;

    @InjectMocks
    private UrlShortenerServiceImpl serviceImpl;

    private static final String VALID_LONG_URL = "https://www.test.com";
    private static final String VALID_SHORT_URL = "Ye35GM";

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
        UrlShortenerException exception = assertThrows(UrlShortenerException.class,
                () -> serviceImpl.generateShortUrl(url));
        verify(urlShortenerRepository, never()).save(any(UrlDataEntity.class));
        assertEquals(INVALID_URL, exception.getMessage());
    }

    @Test
    void testGetShortUrlWhenValidUrlThenGeneratesShortUrl() throws UrlShortenerException {
        when(urlShortenerRepository.save(any(UrlDataEntity.class))).thenReturn(stubUrlDataEntity());
        String generatedShortUrl = serviceImpl.generateShortUrl(VALID_LONG_URL);
        verify(urlShortenerRepository, times(1)).save(dataEntityArgumentCaptor.capture());
        assertNotNull(generatedShortUrl);
        assertEquals(VALID_LONG_URL, dataEntityArgumentCaptor.getValue().getLongUrl());
    }

    @Test
    void testGetLongUrlWhenEmptyShortUrlThenThrowException() {
        UrlShortenerException exception = assertThrows(UrlShortenerException.class,
                () -> serviceImpl.getOriginalUrl(""));
        assertEquals(SHORT_URL_CANNOT_BE_EMPTY, exception.getMessage());
    }

    private static Stream<Arguments> invalidShortUrlData() {
        return Stream.of(
                Arguments.of("abc"),
                Arguments.of("abcdefg2"),
                Arguments.of("abc.efga"),
                Arguments.of("abc efg ")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidShortUrlData")
    void testGetLongUrlWhenSHortUrlIncorrectSizeShortUrlThenThrowException(String shortUrl) {
        UrlShortenerException exception = assertThrows(UrlShortenerException.class,
                () -> serviceImpl.getOriginalUrl(shortUrl));
        assertEquals(INVALID_URL, exception.getMessage());
    }

    @Test
    void testGetLongUrlWhenIdNotFoundThenThrowException() {
        UrlShortenerException exception = assertThrows(UrlShortenerException.class,
                () -> serviceImpl.getOriginalUrl(VALID_SHORT_URL));
        assertEquals(NOT_FOUND_IN_DATABASE, exception.getMessage());
    }

    @Test
    void testValidateEncodedAndDecodedValuesMatch() throws UrlShortenerException {
        UrlDataEntity urlDataEntity = stubUrlDataEntity();
        when(urlShortenerRepository.save(any(UrlDataEntity.class))).thenReturn(urlDataEntity);
        String generatedShortUrl = serviceImpl.generateShortUrl(VALID_LONG_URL);

        when(urlShortenerRepository.findById(anyInt())).thenReturn(Optional.of(urlDataEntity));
        String longUrl = serviceImpl.getOriginalUrl(generatedShortUrl);
        assertEquals(VALID_LONG_URL, longUrl);
    }

    private UrlDataEntity stubUrlDataEntity() {
        Random random = new Random();
        return UrlDataEntity.builder().id(random.nextInt()).longUrl(VALID_LONG_URL).build();
    }

}