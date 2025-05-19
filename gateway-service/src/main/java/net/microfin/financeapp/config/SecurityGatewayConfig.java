package net.microfin.financeapp.config;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityGatewayConfig {
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated())
                .csrf(csrfSpec -> csrfSpec.disable())
                .oauth2ResourceServer(
                        oath -> oath.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
