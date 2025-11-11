package net.microfin.financeapp.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(String serverUrl, String realm, String username, String password) {
}
