package net.microfin.financeapp.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.OncePerRequestFilter;


@Configuration
public class WebConfig {

    @Bean
    public OncePerRequestFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
