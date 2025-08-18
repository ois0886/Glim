package com.lovedbug.geulgwi.docs;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import static io.restassured.config.EncoderConfig.encoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTestSupport {

    @LocalServerPort
    int port;

    protected RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        this.spec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint()))
            .build();

        RestAssured.config = new RestAssuredConfig()
            .encoderConfig(encoderConfig().defaultContentCharset("UTF-8"));
    }
}
