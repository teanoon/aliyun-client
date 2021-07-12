package com.example.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.AuthorizeSecurityGroupRequest;
import com.aliyun.ecs20140526.models.DescribeSecurityGroupAttributeRequest;
import com.aliyun.ecs20140526.models.DescribeSecurityGroupAttributeResponseBody.DescribeSecurityGroupAttributeResponseBodyPermissionsPermission;
import com.aliyun.ecs20140526.models.RevokeSecurityGroupRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);

    private static final String IP_DETECT_API = "https://ip.cn/api/index?ip=&type=0";
    private static final String REGION = "cn-hongkong";
    private static final String SECURITY_GROUP_ID = "sg-j6c7mqm1h3u2w98f11i9";
    private static final String PROTOCOL = "TCP";
    private static final String NIC_TYPE = "intranet";
    private static final String PRIORITY = "1";
    private static final String POLICY = "Accept";

    static final Pattern IP_PATTERN = Pattern.compile(".+?(?<ip>(\\d{2,3}\\.?){4}).+?");

    private final Client client;
    private final HttpClient httpClient;
    private String lastIp;

    public CommonService(Client client) {
        this.client = client;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Scheduled(fixedDelay = 1_000, fixedRate = 60_000)
    public void refreshTrojanPermissions() {
        var newIp = getCurrentIp();
        if (newIp.equals(lastIp)) {
            return;
        }
        var permissions = getTrojanPermissions("hz");
        revokeOldTrojanPermissions(permissions, newIp, "hz");
        addTrojanPermissions(newIp, "hz");
        lastIp = newIp;
    }

    public List<DescribeSecurityGroupAttributeResponseBodyPermissionsPermission> getTrojanPermissions(String office) {
        var request = new DescribeSecurityGroupAttributeRequest();
        request.setRegionId(REGION);
        request.setSecurityGroupId(SECURITY_GROUP_ID);
        try {
            var response = client.describeSecurityGroupAttribute(request);
            return response.body.permissions.permission.stream()
                .filter(permission -> office.equals(permission.getDescription()))
                .collect(Collectors.toList());
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    public void addTrojanPermissions(String newIp, String office) {
        for (String portRange : new String[] {"80/80", "443/443"}) {
            var request = new AuthorizeSecurityGroupRequest();
            request.setRegionId(REGION);
            request.setSecurityGroupId(SECURITY_GROUP_ID);
            request.setIpProtocol(PROTOCOL);
            request.setPortRange(portRange);
            request.setNicType(NIC_TYPE);
            request.setSourceCidrIp(newIp + "/32");
            request.setPolicy(POLICY);
            request.setPriority(PRIORITY);
            request.setDescription(office);
            try {
                var response = client.authorizeSecurityGroup(request);
                LOGGER.info("ip authorized: {}", response.body.toMap());
            } catch (Exception exp) {
                throw new RuntimeException(exp);
            }
        }
    }

    public void revokeOldTrojanPermissions(List<DescribeSecurityGroupAttributeResponseBodyPermissionsPermission> permissions, String newIp, String office) {
        for (DescribeSecurityGroupAttributeResponseBodyPermissionsPermission permission : permissions) {
            var revoke = new RevokeSecurityGroupRequest();
            revoke.setRegionId(REGION);
            revoke.setSecurityGroupId(SECURITY_GROUP_ID);
            revoke.setPortRange(permission.getPortRange());
            revoke.setIpProtocol(permission.getIpProtocol());
            revoke.setSourceCidrIp(permission.getSourceCidrIp());
            revoke.setSourcePortRange(permission.getSourcePortRange());
            revoke.setPolicy(permission.getPolicy());
            revoke.setPriority(permission.getPriority());
            revoke.setNicType(permission.getNicType());
            try {
                var response = client.revokeSecurityGroup(revoke);
                LOGGER.info("ip revoked: {}", response.body.toMap());
            } catch (Exception exp) {
                throw new RuntimeException(exp);
            }
        }
    }

    public String getCurrentIp() {
        var request = HttpRequest.newBuilder(URI.create(IP_DETECT_API)).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            var body = response.body();
            var matcher = IP_PATTERN.matcher(body);
            if (matcher.find()) {
                return matcher.group("ip");
            }
            throw new RuntimeException("No ip found in: " + body);
        } catch (IOException | InterruptedException exp) {
            throw new RuntimeException(exp);
        }
    }

}
