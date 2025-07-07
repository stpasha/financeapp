package net.microfin.financeapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("exceptions")
@Getter
@Setter
public class ExceptionsProperties {
    private String insufficientFundsFailure;
    private String currencyNotFoundFailure;
    private String accNotFoundFailure;
    private String incorectSourceAccountFailure;
}
