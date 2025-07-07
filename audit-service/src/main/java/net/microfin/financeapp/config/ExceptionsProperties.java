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
    private String deserFailure;
    private String operationFailure;
}
