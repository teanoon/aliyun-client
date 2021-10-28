package com.example.client.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class IpService {

    private static final String IP_DETECT_API = "https://ip.cn/api/index?ip=&type=0";
    static final Pattern IP_PATTERN = Pattern.compile(".+?(?<ip>(\\d{2,3}\\.?){4}).+?");

    private final HttpClient httpClient;

    public IpService() {
        this.httpClient = HttpClient.newHttpClient();
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
            throw new RuntimeException(exp.getMessage(), exp);
        }
    }

}
