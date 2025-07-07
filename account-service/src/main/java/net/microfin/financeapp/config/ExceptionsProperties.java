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
    private String makeAccFailure;
    private String searchAccFailure;
    private String makeUserFailure;
    private String searchUserFailure;
    private String deserFailure;
    private String operationFailure;
    private String passEditFailure;
}
