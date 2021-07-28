package com.example.servicepoller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@AllArgsConstructor
public class RestTemplateConfig {

    private final ObjectMapper objectMapper;
    private final RestClientProperties restClientProperties;

    @Bean
    public RestTemplate restTemplate() {
        val mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        val restTemplate = new RestTemplateBuilder().build();
        val factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(HttpClientBuilder.create()
                                               .setMaxConnPerRoute(restClientProperties.getMaxConnectionsPerRoute())
                                               .setMaxConnTotal(restClientProperties.getMaxConnections())
                                               .build());
        factory.setConnectionRequestTimeout(restClientProperties.getConnectionRequestTimeout());
        factory.setConnectTimeout(restClientProperties.getConnectionTimeout());
        factory.setReadTimeout(restClientProperties.getSocketReadTimeout());
        restTemplate.setRequestFactory(factory);

        val messageConverters = restTemplate.getMessageConverters();
        messageConverters.removeIf(m -> m.getClass()
                                         .equals(MappingJackson2HttpMessageConverter.class));
        messageConverters.add(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }
}
