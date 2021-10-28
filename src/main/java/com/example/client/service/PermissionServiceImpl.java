package com.example.client.service;

import java.util.List;
import java.util.stream.Collectors;

import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.AuthorizeSecurityGroupRequest;
import com.aliyun.ecs20140526.models.DescribeSecurityGroupAttributeRequest;
import com.aliyun.ecs20140526.models.DescribeSecurityGroupAttributeResponseBody.DescribeSecurityGroupAttributeResponseBodyPermissionsPermission;
import com.aliyun.ecs20140526.models.RevokeSecurityGroupRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionServiceImpl.class);

    private static final String PROTOCOL = "TCP";
    private static final String NIC_TYPE = "intranet";
    private static final String PRIORITY = "1";
    private static final String POLICY = "Accept";

    private final Client client;

    public PermissionServiceImpl(Client client) {
        this.client = client;
    }

    @Override
    public List<DescribeSecurityGroupAttributeResponseBodyPermissionsPermission> getPermissions(String region, String securityGroupId, String office) {
        var request = new DescribeSecurityGroupAttributeRequest();
        request.setRegionId(region);
        request.setSecurityGroupId(securityGroupId);
        try {
            var response = client.describeSecurityGroupAttribute(request);
            return response.body.permissions.permission.stream()
                .filter(permission -> office.equals(permission.getDescription()))
                .collect(Collectors.toList());
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void addPermissions(String region, String securityGroupId, String newIp, String portRange, String office) {
        var request = new AuthorizeSecurityGroupRequest();
        request.setRegionId(region);
        request.setSecurityGroupId(securityGroupId);
        request.setIpProtocol(PROTOCOL);
        request.setPortRange(portRange);
        request.setNicType(NIC_TYPE);
        request.setSourceCidrIp(newIp + "/32");
        request.setPolicy(POLICY);
        request.setPriority(PRIORITY);
        request.setDescription(office);
        try {
            var response = client.authorizeSecurityGroup(request);
            LOGGER.info("{} {} ip authorized: {}", region, office, response.body.toMap());
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void revokeOldPermissions(String region, String securityGroupId, List<DescribeSecurityGroupAttributeResponseBodyPermissionsPermission> permissions, String office) {
        for (DescribeSecurityGroupAttributeResponseBodyPermissionsPermission permission : permissions) {
            var revoke = new RevokeSecurityGroupRequest();
            revoke.setRegionId(region);
            revoke.setSecurityGroupId(securityGroupId);
            revoke.setPortRange(permission.getPortRange());
            revoke.setIpProtocol(permission.getIpProtocol());
            revoke.setSourceCidrIp(permission.getSourceCidrIp());
            revoke.setSourcePortRange(permission.getSourcePortRange());
            revoke.setPolicy(permission.getPolicy());
            revoke.setPriority(permission.getPriority());
            revoke.setNicType(permission.getNicType());
            try {
                var response = client.revokeSecurityGroup(revoke);
                LOGGER.info("{} {} ip revoked: {}", region, office, response.body.toMap());
            } catch (Exception exp) {
                throw new RuntimeException(exp);
            }
        }
    }

}
