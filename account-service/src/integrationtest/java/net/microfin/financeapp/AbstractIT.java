package net.microfin.financeapp;
import liquibase.command.CommandScope;
import liquibase.exception.CommandExecutionException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testcontainers.containers.PostgreSQLContainer;


public abstract class AbstractIT extends AbstractTestNGSpringContextTests {
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

    static {
        postgres.start();
    }

    static void applyMigrations(String jdbcUrl, String username, String password) {
        try {
            new CommandScope("update")
                    .addArgumentValue("changelogFile", "db/changelog/db.changelog-master.xml")
                    .addArgumentValue("url", jdbcUrl + "&currentSchema=account_info")
                    .addArgumentValue("username", username)
                    .addArgumentValue("password", password)
                    .addArgumentValue("contexts", "!exclude-db-create")
                    .addArgumentValue("duplicateFileMode", "WARN")
                    .execute();
        } catch (CommandExecutionException e) {
            throw new RuntimeException("Failed to execute Liquibase migrations", e);
        }
    }
}
