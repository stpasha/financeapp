package net.microfin.financeapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "client.uri")
@Getter
@Setter
public class UriConfig {
    private String account;
    private String audit;
    private String notification;
}

