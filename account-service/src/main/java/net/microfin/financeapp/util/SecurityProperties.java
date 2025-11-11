package net.microfin.financeapp.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.zbankapi")
public record SecurityProperties(String clientId, String clientSecret) {
}
