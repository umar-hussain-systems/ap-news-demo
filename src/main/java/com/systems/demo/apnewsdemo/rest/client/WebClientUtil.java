package com.systems.demo.apnewsdemo.rest.client;

import com.systems.demo.apnewsdemo.dto.web.client.WebClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientUtil {

    @Autowired
    WebClient webClient;
    public WebClientResponse getResponse() {
        Mono<WebClientResponse> employeeMono = webClient.get()
            .uri("/employees")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(WebClientResponse.class);

        return employeeMono.block();
    }

}
