package net.microfin.financeapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
@Configuration
public class SecurityConfiguration {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/signup", "/signup/**", "/static/**", "/actuator/**", "/").permitAll()
                                .anyRequest().authenticated())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedPage("/access-denied"))
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/keycloak")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
                )
                .oauth2Client(Customizer.withDefaults())
                .logout(logout -> logout
                        // Регистрируем OidcClientInitiatedLogoutSuccessHandler
                        .logoutSuccessHandler(oidcLogoutSuccessHandler)
                );
        return httpSecurity.build();
    }

    @Bean
    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri(URI.create("http://localhost:8080").toString());
        return successHandler;
    }

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                // Загружаем стандартного OidcUser
                OidcUser oidcUser = super.loadUser(userRequest);
                Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

                // Извлекаем роли из id_token -> realm_access.roles
                Map<String, Object> claims = oidcUser.getIdToken().getClaims();
                Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
                if (realmAccess != null) {
                    Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                    if (roles != null) {
                        roles.forEach(role -> mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    }
                }

                // Возвращаем OidcUser с маппингом ролей
                return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }
        };
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

}
