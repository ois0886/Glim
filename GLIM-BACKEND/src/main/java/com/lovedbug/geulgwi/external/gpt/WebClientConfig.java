package com.lovedbug.geulgwi.external.gpt;

import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${openai.key}")
    private String apiKey;

    @Value("${openai.url}")
    private String baseUrl;

    @Bean
    public WebClient openAiWebClient() {

        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer ->
                configurer.defaultCodecs()
                    .maxInMemorySize(20 * 1024 * 1024)
            ).build();

        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .exchangeStrategies(strategies)
            .build();
    }
}

