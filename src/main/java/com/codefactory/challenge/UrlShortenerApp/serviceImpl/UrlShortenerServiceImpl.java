package com.codefactory.challenge.UrlShortenerApp.serviceImpl;

import ch.qos.logback.core.util.StringUtil;
import com.codefactory.challenge.UrlShortenerApp.entity.UrlDataEntity;
import com.codefactory.challenge.UrlShortenerApp.repository.UrlShortenerRepository;
import com.codefactory.challenge.UrlShortenerApp.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String SHORT_URL_CANNOT_BE_EMPTY = "Short URL cannot be empty.";
    public static final String INVALID_URL = "Invalid Url.";
    public static final String NOT_FOUND_IN_DATABASE = "Url not found in database for given short url.";
    private static final int SHORT_URL_LENGTH = 7;
    private static final String SHORT_URL_PATTERN = "[a-zA-z0-9]*";


    private final UrlShortenerRepository urlShortenerRepository;

    @Override
    public String generateShortUrl(String longUrl) throws Exception {
        isValidUrl(longUrl);

        UrlDataEntity urlDataEntity = UrlDataEntity.builder().longUrl(longUrl).build();
        UrlDataEntity urlDataEntitySaved = urlShortenerRepository.save(urlDataEntity);
        return encodeToBase62(urlDataEntitySaved.getId());
    }

    @Override
    public String getLongUrl(String shortUrl) throws Exception {
        isValidShortUrl(shortUrl);
        int decodedNumber = decodeFromBase62(shortUrl);

        Optional<UrlDataEntity> urlDataEntity = urlShortenerRepository.findById(decodedNumber);
        if (urlDataEntity.isEmpty()) {
            throw new Exception(NOT_FOUND_IN_DATABASE);
        }
        return urlDataEntity.get().getLongUrl();
    }

    private void isValidShortUrl(String shortUrl) throws Exception {
        if (StringUtil.isNullOrEmpty(shortUrl))
            throw new Exception(SHORT_URL_CANNOT_BE_EMPTY);
        if (shortUrl.length() != SHORT_URL_LENGTH)
            throw new Exception(INVALID_URL);
        if (!shortUrl.matches(SHORT_URL_PATTERN))
            throw new Exception(INVALID_URL);
    }

    private int decodeFromBase62(String shortUrl) {
        int decodedNumber = 0;

        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            char character = shortUrl.charAt(i);
            decodedNumber += BASE62_CHARACTERS.indexOf(character) * Math.pow(62, SHORT_URL_LENGTH - 1 - i);
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

        while (stringBuilder.length() < SHORT_URL_LENGTH) {
            stringBuilder.insert(0, "0");
        }
        return stringBuilder.toString();
    }

    private void isValidUrl(String url) throws Exception {
        try {
            URI.create(url).toURL();
        } catch (Exception e) {
            throw new Exception(INVALID_URL);
        }
    }
}
