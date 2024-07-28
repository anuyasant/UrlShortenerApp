package com.codefactory.challenge.UrlShortenerApp.serviceImpl;

import ch.qos.logback.core.util.StringUtil;
import com.codefactory.challenge.UrlShortenerApp.entity.UrlDataEntity;
import com.codefactory.challenge.UrlShortenerApp.exception.UrlShortenerException;
import com.codefactory.challenge.UrlShortenerApp.repository.UrlShortenerRepository;
import com.codefactory.challenge.UrlShortenerApp.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String SHORT_URL_CANNOT_BE_EMPTY = "Short URL cannot be empty.";
    public static final String INVALID_URL = "Invalid Url.";
    public static final String NOT_FOUND_IN_DATABASE = "Url not found in database for given short url.";
    private static final String SHORT_URL_PATTERN = "[a-zA-z0-9]*";

    @Value("${app.maxShortUrlLength:6}")
    private int maxShortUrlLength;


    private final UrlShortenerRepository urlShortenerRepository;

    @Override
    public String generateShortUrl(String longUrl) throws UrlShortenerException {
        isValidUrl(longUrl);

        UrlDataEntity urlDataEntity = UrlDataEntity.builder().longUrl(longUrl).build();
        UrlDataEntity urlDataEntitySaved = urlShortenerRepository.save(urlDataEntity);
        return encodeToBase62(urlDataEntitySaved.getId());
    }

    @Override
    public String getOriginalUrl(String shortUrl) throws UrlShortenerException {
        isValidShortUrl(shortUrl);
        int decodedNumber = decodeFromBase62(shortUrl);

        Optional<UrlDataEntity> urlDataEntity = urlShortenerRepository.findById(decodedNumber);
        if (urlDataEntity.isEmpty()) {
            throw new UrlShortenerException(NOT_FOUND_IN_DATABASE, HttpStatus.NOT_FOUND);
        }
        return urlDataEntity.get().getLongUrl();
    }

    private void isValidShortUrl(String shortUrl) throws UrlShortenerException {
        if (StringUtil.isNullOrEmpty(shortUrl))
            throw new UrlShortenerException(SHORT_URL_CANNOT_BE_EMPTY, NOT_ACCEPTABLE);
        if (shortUrl.length() != maxShortUrlLength)
            throw new UrlShortenerException(INVALID_URL, BAD_REQUEST);
        if (!shortUrl.matches(SHORT_URL_PATTERN))
            throw new UrlShortenerException(INVALID_URL, BAD_REQUEST);
    }

    private int decodeFromBase62(String shortUrl) {
        int decodedNumber = 0;

        for (int i = 0; i < maxShortUrlLength; i++) {
            char character = shortUrl.charAt(i);
            decodedNumber += BASE62_CHARACTERS.indexOf(character) * Math.pow(62, maxShortUrlLength - 1 - i);
        }
        return decodedNumber;
    }

    private String encodeToBase62(Integer number) {
        StringBuilder stringBuilder = new StringBuilder();

        while (number > 0) {
            int remainder = number % 62;
            stringBuilder.insert(0, BASE62_CHARACTERS.charAt(remainder));
            number /= 62;
        }

        while (stringBuilder.length() < maxShortUrlLength) {
            stringBuilder.insert(0, "0");
        }
        return stringBuilder.toString();
    }

    private void isValidUrl(String url) throws UrlShortenerException {
        try {
            URI.create(url).toURL();
        } catch (Exception e) {
            log.error("Exception occurred while validating URL: {}", e.getMessage());
            throw new UrlShortenerException(INVALID_URL, HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
