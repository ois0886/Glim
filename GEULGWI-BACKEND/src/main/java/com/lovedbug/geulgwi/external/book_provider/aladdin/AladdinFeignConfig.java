package com.lovedbug.geulgwi.external.book_provider.aladdin;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AladdinFeignConfig {

    @Value("${aladin.ttbkey}")
    private String ttbkey;

    @Bean
    public RequestInterceptor aladinRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.query("ttbkey", ttbkey);
        };
    }
}
