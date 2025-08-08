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
import io.javalin.validation.ValidationException;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
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
        UrlCheck testCheck = new UrlCheck();
        testCheck.setId(1);
        testCheck.setStatusCode(200);

        try (MockedStatic<UrlRepository> mockedRepo = mockStatic(UrlRepository.class);
             MockedStatic<UrlCheckRepository> mockedCheckRepo = mockStatic(UrlCheckRepository.class)) {
            mockedRepo.when(() -> UrlRepository.findById(1)).thenReturn(testUrl);
            mockedCheckRepo.when(() -> UrlCheckRepository.findAllById(1)).thenReturn(List.of(testCheck));

            Validator<Integer> mockValidator = mock(Validator.class);
            when(mockValidator.get()).thenReturn(1);
            when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(mockValidator);

            UrlController.show(ctx);

            verify(ctx).render(eq("urls/show.jte"), any(Map.class));
            mockedRepo.verify(() -> UrlRepository.findById(1));
            mockedCheckRepo.verify(() -> UrlCheckRepository.findAllById(1));
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

    @Test
    public void buildUrlTest() {
        UrlController.buildUrl(ctx);
        verify(ctx).render(eq("urls/build.jte"), any(Map.class));
    }

    @Test
    public void createUrlValidationErrorTest() {
        Validator<String> mockValidator = mock(Validator.class);
        when(mockValidator.check(any(), anyString())).thenThrow(new ValidationException(Map.of()));
        when(ctx.formParamAsClass("url", String.class)).thenReturn(mockValidator);
        when(ctx.formParam("url")).thenReturn("invalid-url");

        UrlController.createUrl(ctx);

        verify(ctx).render(eq("urls/build.jte"), any(Map.class));
    }

    @Test
    public void createUrlEmptyTest() {
        Validator<String> mockValidator = mock(Validator.class);

        when(mockValidator.check(any(), anyString())).thenThrow(new ValidationException(Map.of()));
        when(ctx.formParamAsClass("url", String.class)).thenReturn(mockValidator);
        when(ctx.formParam("url")).thenReturn("");

        UrlController.createUrl(ctx);

        verify(ctx).render(eq("urls/build.jte"), any(Map.class));
    }

    @Test
    public void showUrlsTest() {
        try (MockedStatic<UrlRepository> mockedUrlRepo = mockStatic(UrlRepository.class);
             MockedStatic<UrlCheckRepository> mockedCheckRepo = mockStatic(UrlCheckRepository.class)) {

            Url testUrl = new Url();
            testUrl.setId(1);
            testUrl.setName("https://example.com");

            UrlCheck testCheck = new UrlCheck();
            testCheck.setId(1);
            testCheck.setStatusCode(200);

            mockedUrlRepo.when(UrlRepository::getAllUrls).thenReturn(List.of(testUrl));
            mockedCheckRepo.when(() -> UrlCheckRepository.findById(1)).thenReturn(testCheck);
            when(ctx.consumeSessionAttribute("flash")).thenReturn("Test flash message");

            UrlController.showUrls(ctx);

            verify(ctx).render(eq("urls/index.jte"), any(Map.class));
            verify(ctx).consumeSessionAttribute("flash");
        }
    }

    @Test
    public void checkUrlWithNullUrlTest() {
        try (MockedStatic<UrlRepository> mockedRepo = mockStatic(UrlRepository.class)) {
            Validator<Integer> mockValidator = mock(Validator.class);
            when(mockValidator.get()).thenReturn(1);
            when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(mockValidator);

            mockedRepo.when(() -> UrlRepository.findById(1)).thenReturn(null);

            UrlController.checkUrl(ctx);

            verify(ctx, never()).redirect(anyString());
        }
    }

    @Test
    public void checkUrlWithValidUrlTest() {
        try (MockedStatic<UrlRepository> mockedUrlRepo = mockStatic(UrlRepository.class);
             MockedStatic<UrlCheckService> mockedCheckService = mockStatic(UrlCheckService.class);
             MockedStatic<UrlCheckRepository> mockedCheckRepo = mockStatic(UrlCheckRepository.class)) {

            Url testUrl = new Url();
            testUrl.setId(1);
            testUrl.setName("https://example.com");

            UrlCheck testCheck = new UrlCheck();
            testCheck.setStatusCode(200);

            Validator<Integer> mockValidator = mock(Validator.class);
            when(mockValidator.get()).thenReturn(1);
            when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(mockValidator);

            mockedUrlRepo.when(() -> UrlRepository.findById(1)).thenReturn(testUrl);
            mockedCheckService.when(() -> UrlCheckService.urlCheck("https://example.com", 1)).thenReturn(testCheck);

            UrlController.checkUrl(ctx);

            verify(ctx).redirect("/urls");
            mockedCheckRepo.verify(() -> UrlCheckRepository.save(testCheck));
        }
    }

    @Test
    public void checkUrlServiceWithH1Test() {
        mockWebServer.enqueue(new MockResponse.Builder()
                .body("""
                <html>
                    <head>
                        <title>Test title</title>
                    </head>
                    <body>
                        <h1>Main heading</h1>
                        <description>Test description content</description>
                    </body>
                </html>
                """)
                .code(200)
                .build()
        );

        String testUrl = mockWebServer.url("test-h1").toString();
        UrlCheck result = UrlCheckService.urlCheck(testUrl, 1);

        assertEquals(200, result.getStatusCode());
        assertEquals("Test title", result.getTitle());
        assertEquals("Main heading", result.getH1());
        assertEquals("Test description content", result.getDescription());
    }

    @Test
    public void checkUrlServiceWithEmptyElementsTest() {
        mockWebServer.enqueue(new MockResponse.Builder()
                .body("""
                <html>
                    <body>
                        <p>Simple page without title, h1 or description</p>
                    </body>
                </html>
                """)
                .code(404)
                .build()
        );

        String testUrl = mockWebServer.url("empty-elements").toString();
        UrlCheck result = UrlCheckService.urlCheck(testUrl, 2);

        assertEquals(404, result.getStatusCode());
        assertEquals("", result.getTitle());
        assertEquals("", result.getH1());
        assertEquals("", result.getDescription());
        assertEquals(2, result.getUrlId());
    }

    @Test
    public void createUrlDuplicateTest() {
        try (MockedStatic<UrlRepository> mockedRepo = mockStatic(UrlRepository.class)) {
            Validator<String> mockValidator = mock(Validator.class);

            when(mockValidator.get()).thenReturn("https://example.com");
            when(mockValidator.check(any(), anyString())).thenReturn(mockValidator);

            ValidationException validationException = new ValidationException(Map.of());
            when(mockValidator.check(any(), anyString())).thenThrow(validationException);

            when(ctx.formParamAsClass("url", String.class)).thenReturn(mockValidator);
            when(ctx.formParam("url")).thenReturn("https://example.com");

            UrlController.createUrl(ctx);

            verify(ctx).render(eq("urls/build.jte"), any(Map.class));
        }
    }

    @Test
    public void createUrlWithPortTest() {
        Validator<String> mockValidator = mock(Validator.class);
        String testUrl = "https://example.com:8080";

        when(mockValidator.get()).thenReturn(testUrl);
        when(mockValidator.check(any(), anyString())).thenReturn(mockValidator);
        when(ctx.formParamAsClass("url", String.class)).thenReturn(mockValidator);

        UrlController.createUrl(ctx);

        verify(ctx).status(HttpStatus.CREATED);
        verify(ctx).redirect("/urls");
        verify(ctx).sessionAttribute("flash", "Url has been created!");
    }

    @Test
    public void createUrlHttpsTest() {
        Validator<String> mockValidator = mock(Validator.class);
        String testUrl = "https://secure-example.com";

        when(mockValidator.get()).thenReturn(testUrl);
        when(mockValidator.check(any(), anyString())).thenReturn(mockValidator);
        when(ctx.formParamAsClass("url", String.class)).thenReturn(mockValidator);

        UrlController.createUrl(ctx);

        verify(ctx).status(HttpStatus.CREATED);
        verify(ctx).redirect("/urls");
        verify(ctx).sessionAttribute("flash", "Url has been created!");
    }
}
