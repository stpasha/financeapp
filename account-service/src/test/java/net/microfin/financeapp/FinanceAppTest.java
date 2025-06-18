package net.microfin.financeapp;

import net.microfin.financeapp.config.KeyCloakConfig;
import net.microfin.financeapp.config.TestConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = ServiceApplication.class)
@ImportAutoConfiguration(exclude = KeyCloakConfig.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
public @interface  FinanceAppTest {
}
