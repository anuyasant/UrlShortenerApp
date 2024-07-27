package com.codefactory.challenge.UrlShortenerApp.controller;

import com.codefactory.challenge.UrlShortenerApp.exception.UrlShortenerException;
import com.codefactory.challenge.UrlShortenerApp.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.codefactory.challenge.UrlShortenerApp.serviceImpl.UrlShortenerServiceImpl.INVALID_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UrlShortenerController.class)
class UrlShortenerControllerTest {

    @MockBean
    private UrlShortenerService urlService;

    @InjectMocks
    private UrlShortenerController urlController;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    private static final String TEST_SHORT_URL = "fGhgx43";
    private static final String TEST_LONG_URL = "https://www.test.com";

    @BeforeEach
    protected void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGenerateShortURL() throws Exception {
        String uri = "/app/create";
        String urlAppend = "localhost:8080/";
        when(urlService.generateShortUrl(TEST_LONG_URL)).thenReturn(TEST_SHORT_URL);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .content(TEST_LONG_URL)).andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(urlAppend + TEST_SHORT_URL, mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void testGetShortUrlAndRedirect() throws Exception {
        String uri = "/" + TEST_SHORT_URL;
        when(urlService.getOriginalUrl(TEST_SHORT_URL)).thenReturn(TEST_LONG_URL);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getHeaders("Location").contains(TEST_LONG_URL));
        assertEquals(TEST_LONG_URL, mvcResult.getResponse().getRedirectedUrl());
    }

    @Test
    void testHandleException() throws Exception {
        String uri = "/app/create";
        when(urlService.generateShortUrl(anyString()))
                .thenThrow(new UrlShortenerException(INVALID_URL, HttpStatus.NOT_ACCEPTABLE));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .content(TEST_LONG_URL)).andReturn();
        assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), mvcResult.getResponse().getStatus());
        assertEquals(INVALID_URL, mvcResult.getResponse().getContentAsString());

    }

}