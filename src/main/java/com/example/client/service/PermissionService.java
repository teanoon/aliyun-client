package com.example.client.service;

import java.util.List;

import com.aliyun.ecs20140526.models.DescribeSecurityGroupAttributeResponseBody.DescribeSecurityGroupAttributeResponseBodyPermissionsPermission;

public interface PermissionService {

    List<DescribeSecurityGroupAttributeResponseBodyPermissionsPermission> getPermissions(String region, String securityGroupId, String portRange, String office);

    void addPermissions(String region, String securityGroupId, String newIp, String portRange, String protocol, String office);

    void revokeOldPermissions(String region, String securityGroupId, List<DescribeSecurityGroupAttributeResponseBodyPermissionsPermission> permissions, String office);

}
