package carsharing.carsharingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerTest {
    protected static String removeAllSqlFilePath;
    protected static String insertDefaultSqlFilePath;

    protected static MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup
                        .SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeEach
    void setupDatabase(@Autowired DataSource dataSource) {
        executeScript(dataSource, removeAllSqlFilePath);
        executeScript(dataSource, insertDefaultSqlFilePath);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        executeScript(dataSource, removeAllSqlFilePath);
    }

    private static void executeScript(DataSource dataSource, String scriptPath) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(scriptPath));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute script: " + scriptPath, e);
        }
    }
}