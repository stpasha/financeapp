package net.microfin.financeapp.config;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.security.OAuth2AccessTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

@EnableFeignClients(basePackages = "net.microfin.financeapp.client")
@Configuration
public class FeignConfig {

//    @Configuration
//    public class FeignClientOAuth2Config {
//
//        @Bean
//        public RequestInterceptor oauth2FeignRequestInterceptor(
//                OAuth2AuthorizedClientManager authorizedClientManager) {
//
//            return requestTemplate -> {
//                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//                // Убедись, что пользователь авторизован
//                if (authentication == null || !authentication.isAuthenticated()) {
//                    return;
//                }
//
//                OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
//                        .withClientRegistrationId("keycloak")
//                        .principal(authentication)
//                        .build();
//
//                OAuth2AuthorizedClient client =
//                        authorizedClientManager.authorize(authorizeRequest);
//
//                if (client != null && client.getAccessToken() != null) {
//                    String token = client.getAccessToken().getTokenValue();
//                    requestTemplate.header("Authorization", "Bearer " + token);
//                }
//            };
//        }
//
//        @Bean
//        public OAuth2AuthorizedClientManager authorizedClientManager(
//                ClientRegistrationRepository clientRegistrationRepository,
//                OAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//            OAuth2AuthorizedClientProvider authorizedClientProvider =
//                    OAuth2AuthorizedClientProviderBuilder.builder()
//                            .authorizationCode()
//                            .refreshToken() // ← обязательно, если токен может устареть
//                            .build();
//
//            DefaultOAuth2AuthorizedClientManager manager =
//                    new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
//
//            manager.setAuthorizedClientProvider(authorizedClientProvider);
//            return manager;
//        }
//    }
}
