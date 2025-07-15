package net.microfin.financeapp.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "net.microfin.financeapp.client")
@Configuration
public class FeignConfig {
}
