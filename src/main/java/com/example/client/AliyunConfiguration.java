package com.example.client;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "aliyun")
@Data
public class AliyunConfiguration {

    private String accessKey;
    private String accessKeySecret;
    private String endpoint;

    private List<PermissionConfig> permissions;

}
