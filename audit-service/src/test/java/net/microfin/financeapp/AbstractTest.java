package net.microfin.financeapp;


import liquibase.command.CommandScope;
import liquibase.exception.CommandExecutionException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("common_database")
            .withUsername("zbank")
            .withPassword("password")
            .withInitScript("init-script.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        applyMigrations(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    static void applyMigrations(String jdbcUrl, String username, String password) {
        try {
            new CommandScope("update")
                    .addArgumentValue("changelogFile", "db/changelog/db.changelog-master.xml")
                    .addArgumentValue("url", jdbcUrl + "&currentSchema=account_info")
                    .addArgumentValue("username", username)
                    .addArgumentValue("password", password)
                    .addArgumentValue("contexts", "!exclude-db-create")
                    .execute();
        } catch (CommandExecutionException e) {
            throw new RuntimeException("Failed to execute Liquibase migrations", e);
        }
    }
}


