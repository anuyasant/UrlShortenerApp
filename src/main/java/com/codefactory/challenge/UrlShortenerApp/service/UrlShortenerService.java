package com.codefactory.challenge.UrlShortenerApp.service;

import com.codefactory.challenge.UrlShortenerApp.exception.UrlShortenerException;

public interface UrlShortenerService {

    String generateShortUrl(String url) throws UrlShortenerException;

    String getOriginalUrl(String shortUrl) throws UrlShortenerException;
}
