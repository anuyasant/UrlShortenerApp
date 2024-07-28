package com.codefactory.challenge.UrlShortenerApp.controller;

import com.codefactory.challenge.UrlShortenerApp.exception.UrlShortenerException;
import com.codefactory.challenge.UrlShortenerApp.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlShortenerController {
    private final UrlShortenerService urlShortenerService;

    @Value("${app.domain:localhost:8080}")
    private String appDomain;
    private static final String SEPARATOR = "/";

    @PostMapping("/shorturl/create")
    public ResponseEntity<String> generateShortUrl(@RequestBody String longUrl) throws UrlShortenerException {
        String shortUrl = urlShortenerService.generateShortUrl(longUrl);
        return ResponseEntity.status(HttpStatus.OK)
                .body(appDomain.concat(SEPARATOR).concat(shortUrl));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> getLongUrl(@PathVariable("shortUrl") String shortUrl) throws UrlShortenerException {
        String originalUrl = urlShortenerService.getOriginalUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @ExceptionHandler(UrlShortenerException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(UrlShortenerException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getHttpStatus());
    }
}
