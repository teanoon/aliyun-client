package com.example.client;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.client.service.IpService;
import com.example.client.service.PermissionService;

@SpringBootApplication
@EnableScheduling
public class Application {

    private final PermissionService permissionService;
    private final IpService ipService;
    private final List<PermissionConfig> permissionConfigs;

    private String lastIp;

    public Application(
            PermissionService permissionService,
            IpService ipService,
            AliyunConfiguration configuration) {
        this.permissionService = permissionService;
        this.ipService = ipService;
        this.permissionConfigs = configuration.getPermissions();
    }

    @Scheduled(initialDelayString = "1000", fixedRateString = "60000")
    public void refreshPermissions() {
        var newIp = ipService.getCurrentIp();
        if (newIp.equals(lastIp)) {
            return;
        }
        permissionConfigs.forEach(config -> {
            var permissions = permissionService.getPermissions(config.getRegion(), config.getSecurityGroupId(), config.getPortRange(), config.getComment());
            permissionService.revokeOldPermissions(config.getRegion(), config.getSecurityGroupId(), permissions, config.getComment());
            permissionService.addPermissions(config.getRegion(), config.getSecurityGroupId(), newIp, config.getPortRange(), config.getProtocol(), config.getComment());
        });
        lastIp = newIp;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
