package com.example.msa.event.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class MemberServiceClient {

    private static final Logger log = LoggerFactory.getLogger(MemberServiceClient.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final ZoneId zoneId;

    public MemberServiceClient(@Value("${member-service.base-url:http://localhost:8081}") String baseUrl,
                               @Value("${app.default-zone:Asia/Seoul}") String zoneId) {
        this.baseUrl = baseUrl;
        this.zoneId = ZoneId.of(zoneId);
    }

    public MemberProfile fetchProfile(Long memberId) {
        try {
            ResponseEntity<MemberProfile> response = restTemplate.getForEntity(
                    baseUrl + "/members/" + memberId,
                    MemberProfile.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("회원 정보 조회 실패 memberId={}", memberId, e);
            return null;
        }
    }

    public boolean matchesBirthday(MemberProfile profile, String conditionValue) {
        if (profile == null || profile.birthday() == null) {
            return false;
        }
        String monthDay = profile.birthday().substring(5);
        return monthDay.equals(conditionValue);
    }

    public BigDecimal parseAmount(String value) {
        return new BigDecimal(value);
    }

    public ZoneId zone() {
        return zoneId;
    }

    public record MemberProfile(Long id, String name, String email, String birthday) {
    }
}
