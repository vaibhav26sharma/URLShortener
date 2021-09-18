package com.java.urlshortener.urlshortener.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.urlshortener.urlshortener.common.URLValidator;
import com.java.urlshortener.urlshortener.service.URLConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * This is the point of entry for the web application
 * a) It receives requests to shorten the URL & responds with a shortened URL
 * b) Receives a shortened URL & automatically redirect the user to original website
 */
@RestController
public class URLController {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLController.class);
    private final URLConverterService urlConverterService;

    public URLController(URLConverterService urlConverterService) {
        this.urlConverterService = urlConverterService;
    }

    /**
     * We can use ShortenRequest as a parameter coz we declared
     * ShortenRequest as a class & marked it as a @JsonCreator,
     * allowing Jackson (a JSON Serializer) to detect our Request &
     * turn it into a class object.
     *
     * @param shortenRequest
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/shortener", method = RequestMethod.POST, consumes = {"application/json"})
    public String shortenUrl(@RequestBody @Valid final ShortenRequest shortenRequest, HttpServletRequest request) throws Exception {
        LOGGER.info("Received url to shorten: {}  ", shortenRequest.getUrl());
        String longUrl = shortenRequest.getUrl();
        if (URLValidator.INSTANCE.validateURL(longUrl)) {
            String localUrl = request.getRequestURL().toString();
            String shortenedUrl = urlConverterService.shortenUrl(localUrl, shortenRequest.getUrl());
            LOGGER.info("Shortened URL to: {}", shortenedUrl);
            return shortenedUrl;
        }
        throw new Exception("Please enter a valid URL");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RedirectView redirectUrl(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOGGER.info("Receieved shortened url to redirect: {}", id);
        String redirectUrlString = urlConverterService.getLongURLFromID(id);
        LOGGER.info("Original URL: {}", redirectUrlString);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://" + redirectUrlString);
        return redirectView;
    }
}


class ShortenRequest {
    private String url;

    @JsonCreator
    public ShortenRequest() {
    }

    public ShortenRequest(@JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
