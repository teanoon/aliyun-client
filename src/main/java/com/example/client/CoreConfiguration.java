package com.example.client;

import com.aliyun.ecs20140526.Client;
import com.aliyun.teaopenapi.models.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CoreConfiguration.AliyunConfiguration.class)
public class CoreConfiguration {

    @Bean
    public Client client(AliyunConfiguration configuration) throws Exception {
        var config = new Config()
            .setAccessKeyId(configuration.getAccessKey())
            .setAccessKeySecret(configuration.getAccessKeySecret())
            .setEndpoint(configuration.getEndpoint());
        return new Client(config);
    }

    @ConfigurationProperties(prefix = "aliyun")
    static class AliyunConfiguration {

        private String accessKey;
        private String accessKeySecret;
        private String endpoint;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

    }

}
