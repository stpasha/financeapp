package net.microfin.financeapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityGatewayConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/user").permitAll()
                .anyExchange().authenticated())
                .csrf(csrfSpec -> csrfSpec.disable())
                .oauth2ResourceServer(
                        oath -> oath.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
