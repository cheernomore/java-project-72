package hexlet.code.controller;

import hexlet.code.db.DatabaseConnection;
import hexlet.code.db.DatabaseInitializer;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.service.UrlCheckService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
public final class UrlControllerTest {

    private final Context ctx = mock(Context.class);
    private Connection connection;
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
    public void createUrlTest(String testUrlName) {
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

        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, testUrlName);
            var rs = stmt.executeQuery();

            while (rs.next()) {
                assertEquals(testUrlName, rs.getString("name"));
            }
        } catch (SQLException e) {
            log.error("SQL Exception: ", e);
        }
    }

    @Test
    public void showUrlTest() {
        Url testUrl = new Url();
        testUrl.setId(1);
        testUrl.setName("https://google.com");
        testUrl.setCreatedAt(Timestamp.from(Instant.now()));

        UrlCheck testCheck = new UrlCheck();
        testCheck.setId(1);
        testCheck.setStatusCode(200);
        testCheck.setUrlId(1);
        testCheck.setCreatedAt(Timestamp.from(Instant.now()));

        try (MockedStatic<UrlRepository> mockedUrlRepo = mockStatic(UrlRepository.class);
             MockedStatic<UrlCheckRepository> mockedUrlCheckRepo = mockStatic(UrlCheckRepository.class)
        ) {
            mockedUrlRepo.when(() -> UrlRepository.findById(1)).thenReturn(testUrl);
            mockedUrlCheckRepo.when(() -> UrlCheckRepository.findAllByUrlId(1)).thenReturn(List.of(testCheck));

            Validator<Integer> mockValidator = mock(Validator.class);
            when(mockValidator.get()).thenReturn(1);
            when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(mockValidator);

            UrlController.show(ctx);

            verify(ctx).render(eq("urls/show.jte"), any(Map.class));
            mockedUrlRepo.verify(() -> UrlRepository.findById(1));
            mockedUrlCheckRepo.verify(() -> UrlCheckRepository.findAllByUrlId(1));
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
