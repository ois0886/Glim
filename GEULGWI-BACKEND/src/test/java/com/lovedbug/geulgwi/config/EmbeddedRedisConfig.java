package com.lovedbug.geulgwi.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;
import org.springframework.beans.factory.annotation.Value;

@TestConfiguration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;

//    @Value("${spring.data.redis.port:6379}")
//    private int port;

    public EmbeddedRedisConfig(@Value("${spring.data.redis.port}") int port) {
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void startRedis() {
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        this.redisServer.stop();
    }
}
