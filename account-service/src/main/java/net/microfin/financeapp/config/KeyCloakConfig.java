package net.microfin.financeapp.config;

import net.microfin.financeapp.util.KeycloakProperties;
import net.microfin.financeapp.util.SecurityProperties;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakConfig {

    private final KeycloakProperties keycloakProperties;
    private final SecurityProperties securityProperties;

    public KeyCloakConfig(KeycloakProperties keycloakProperties, SecurityProperties securityProperties) {
        this.keycloakProperties = keycloakProperties;
        this.securityProperties = securityProperties;
    }

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .clientId(securityProperties.clientId())
                .clientSecret(securityProperties.clientSecret())
                .username(keycloakProperties.username())
                .password(keycloakProperties.password())
                .realm(keycloakProperties.realm())
                .serverUrl(keycloakProperties.serverUrl())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    @Bean
    public UsersResource usersResource(Keycloak keycloak) {
        return keycloak.realm(keycloakProperties.realm()).users();
    }

}
