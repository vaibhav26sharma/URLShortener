package com.java.urlshortener.urlshortener.service;

import com.java.urlshortener.urlshortener.common.IDConverter;
import com.java.urlshortener.urlshortener.repository.URLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 1.This service takes the original URL and return a shortened URL & vice-versa
 */
@Service
public class URLConverterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLConverterService.class);
    private final URLRepository urlRepository;

    @Autowired
    public URLConverterService(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /**
     * Accepts the URL to be shortened,
     * calls the Repostiory to save the original URL
     * & perform String manipulation using base URL to
     * return a shortened URL
     *
     * @param localURL
     * @param longUrl  - URL to shorten
     * @return
     */
    public String shortenUrl(String localURL, String longUrl) {
        LOGGER.info("Shortening {}", longUrl);
        //Every url will have an id
        Long id = urlRepository.incrementId();
        //Every url will have a unique id
        String uniqueId = IDConverter.INSTANCE.createUniqueID(id);
        urlRepository.saveUrl("url:" + id, longUrl);
        String baseString = formatLocalUrlFromShortener(localURL);
        return baseString + uniqueId;
    }

    public String getLongURLFromID(String uniqueID) throws Exception {
        Long dictionaryKey = IDConverter.INSTANCE.getDictionaryKeyFromBase62UniqueID(uniqueID);
        String longURL = urlRepository.getUrl(dictionaryKey);
        LOGGER.info("Converting shortened URL back to {}", longURL);
        return longURL;
    }

    /**
     * takes the URL used to perform the POST request
     * & obtains the hostname and port number
     *
     * @param localURL
     * @return
     */
    private String formatLocalUrlFromShortener(String localURL) {
        String[] addressComponents = localURL.split("/");
        //Remove the endpoint (last index)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addressComponents.length - 1; i++) {
            sb.append(addressComponents[i]);
            sb.append("/");
        }
        return sb.toString();
    }
}
