package com.java.urlshortener.urlshortener.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.NoSuchElementException;

//Every instance of this class represents one url
//@Repository annotation allows Spring to detect the class
// & include it in the build path
@Repository
public class URLRepository {
    //Jedis is a Redis client for Java
    private final Jedis jedis;
    private final String idKey;
    private final String urlKey;
    private static final Logger LOGGER = LoggerFactory.getLogger(URLRepository.class);

    public URLRepository() {
        this.jedis = new Jedis();
        this.idKey = "id";
        this.urlKey = "url:";
    }

    public URLRepository(Jedis jedis, String idKey, String urlKey) {
        this.jedis = jedis;
        this.idKey = idKey;
        this.urlKey = urlKey;
    }

    public Long incrementId() {
        Long id = jedis.incr(idKey);
        LOGGER.info("Increment ID: {}", id - 1);
        return id - 1;
    }

    public void saveUrl(String key, String longUrl) {
        LOGGER.info("Saving: {} at {}", longUrl, key);
        jedis.hset(urlKey, key, longUrl);
    }

    public String getUrl(Long id) throws Exception {
        LOGGER.info("Retrieving at {}", id);
        String url = jedis.hget(urlKey, "url:" + id);
        LOGGER.info("Retrieved {} at {}", url, id);
        if (url == null) {
            throw new NoSuchElementException("Url at key " + id + "does not exist");
        }
        return url;
    }
}
