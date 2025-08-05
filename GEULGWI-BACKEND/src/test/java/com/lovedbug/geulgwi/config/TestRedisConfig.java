package com.lovedbug.geulgwi.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class TestRedisConfig {

    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.0.11-alpine");

    public static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
            .withExposedPorts(6379);

        REDIS_CONTAINER.start();

        Runtime.getRuntime().addShutdownHook(new Thread(REDIS_CONTAINER::stop));
    }

    public static void overrideRedisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }
}
