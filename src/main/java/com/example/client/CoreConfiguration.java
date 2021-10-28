package com.example.client;

import com.aliyun.ecs20140526.Client;
import com.aliyun.teaopenapi.models.Config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliyunConfiguration.class)
public class CoreConfiguration {

    @Bean
    public Client client(AliyunConfiguration configuration) throws Exception {
        var config = new Config()
            .setAccessKeyId(configuration.getAccessKey())
            .setAccessKeySecret(configuration.getAccessKeySecret())
            .setEndpoint(configuration.getEndpoint());
        return new Client(config);
    }

}
