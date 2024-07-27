package com.codefactory.challenge.UrlShortenerApp.service;

public interface UrlShortenerService {

    String generateShortUrl(String url) throws Exception;

    String getLongUrl(String shortUrl) throws Exception;
}
