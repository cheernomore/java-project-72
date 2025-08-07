package hexlet.code.controller;

import hexlet.code.db.DatabaseConnection;
import hexlet.code.db.DatabaseInitializer;
import hexlet.code.model.UrlCheck;
import hexlet.code.services.UrlCheckService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.Validator;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;

public final class UrlControllerTest {

    private Connection connection;
    private final Context ctx = mock(Context.class);
    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        connection = DatabaseConnection.getDataSource().getConnection();

        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeAll
    static void startUp() {
        DatabaseInitializer.initializeDatabase();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
        mockWebServer.close();
    }

    @ParameterizedTest
    @CsvSource({
        "http://popolam.money, http://popolam.money",
        "http://popolam.eu/path?queryParam=paramExample, http://popolam.eu",
        "http://popolam.com:8080, http://popolam.com:8080",
        "http://popolam.ru:8080/path, http://popolam.ru:8080",
        "http://popolam.io:8080/path?queryParam=paramExample, http://popolam.io:8080"
    })
    public void createUrlTest(String testUrlName) throws SQLException {
        Validator<String> mockValidator = mock(Validator.class);

        when(mockValidator.get()).thenReturn(testUrlName);
        when(mockValidator.check(any(), anyString())).thenReturn(mockValidator);
        when(ctx.formParamAsClass("url", String.class)).thenReturn(mockValidator);

        UrlController.createUrl(ctx);
        verify(ctx).status(HttpStatus.CREATED);
        verify(ctx).redirect("/urls");

        var sql =
                """
                        SELECT name
                        FROM urls
                        WHERE name = ?
                """;

        var stmt = connection.prepareStatement(sql);
        stmt.setString(1, testUrlName);
        var rs = stmt.executeQuery();

        while (rs.next()) {
            assertEquals(testUrlName, rs.getString("name"));
        }
    }

    @Test
    public void checkUrlSuccess() {
        mockWebServer.enqueue(new MockResponse.Builder()
                .body("""
                <html>
                    <head>
                        <title>Test title</title>
                        <meta name="description" content="Test description">
                    </head>
                    <body>
                        <h1>Test heading</h1>
                    </body>
                </html>
                """)
                .code(200)
                .build()
        );

        String testUrl = mockWebServer.url("test").toString();
        UrlCheck result = UrlCheckService.urlCheck(testUrl, 1);

        assertEquals(200, result.getStatusCode());
        assertEquals("Test title", result.getTitle());
    }
}
