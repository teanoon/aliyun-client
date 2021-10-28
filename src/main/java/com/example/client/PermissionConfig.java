package com.example.client;

import lombok.Data;

@Data
public class PermissionConfig {

    private String name;
    private String region;
    private String securityGroupId;
    private String portRange;
    private String comment;

}
