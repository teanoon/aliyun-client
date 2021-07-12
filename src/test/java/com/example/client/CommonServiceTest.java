package com.example.client;

import org.junit.jupiter.api.Test;

public class CommonServiceTest {

    @Test
    public void testIpPattern() {
        var matcher = CommonService.IP_PATTERN.matcher("{\"rs\":1,\"code\":0,\"address\":\"中国  浙江省 杭州市 电信\",\"ip\":\"115.206.200.253\",\"isDomain\":0}");
        matcher.find();
        System.out.println(matcher.group("ip"));
    }

}
