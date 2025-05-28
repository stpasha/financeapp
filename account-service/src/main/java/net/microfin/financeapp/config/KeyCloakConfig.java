package net.microfin.financeapp.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakConfig {
    @Value("${keycloak.server-url}")
    private String keyCloakServerUrl;
    @Value("${keycloak.realm}")
    private String keyCloakRealm;
    @Value("${keycloak.username}")
    private String username;
    @Value("${keycloak.password}")
    private String password;
    @Value("${spring.security.oauth2.client.registration.zbankapi.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.zbankapi.client-secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .realm(keyCloakRealm)
                .serverUrl(keyCloakServerUrl)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    @Bean
    public UsersResource usersResource(Keycloak keycloak) {
        return keycloak.realm(keyCloakRealm).users();
    }

}
