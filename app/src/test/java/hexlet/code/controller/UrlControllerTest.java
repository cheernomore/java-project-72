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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private static Connection connection;
    private MockWebServer mockWebServer;

    @BeforeAll
    static void startUp() throws SQLException {
        DatabaseInitializer.initializeDatabase();
        connection = DatabaseConnection.getDataSource().getConnection();
        connection.setAutoCommit(false);
    }

    @AfterAll
    static void shutdown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
        }
        if (mockWebServer != null) {
            mockWebServer.close();
        }
    }

    @ParameterizedTest
    @CsvSource({
        "http://popolam.money, http://popolam.money",
        "http://popolam.eu/path?queryParam=paramExample, http://popolam.eu",
        "http://popolam.com:8080, http://popolam.com:8080",
        "http://popolam.ru:8080/path, http://popolam.ru:8080",
        "http://popolam.io:8080/path?queryParam=paramExample, http://popolam.io:8080"
    })
    public void createUrlTest(String testUrlName, String expectedUrl) {
        Validator<String> mockValidator = mock(Validator.class);

        when(mockValidator.get()).thenReturn(testUrlName);
        when(mockValidator
                .check(any(), anyString()))
                .thenReturn(mockValidator);

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
            stmt.setString(1, expectedUrl);
            var rs = stmt.executeQuery();

            while (rs.next()) {
                assertEquals(expectedUrl, rs.getString("name"));
            }
        } catch (SQLException e) {
            log.error("SQL Exception: ", e);
        }
    }

    @Test
    public void createUrlWithInvalidNameTest() {
        Validator<String> mockValidator = mock(Validator.class);

        when(mockValidator.get())
                .thenThrow(new ValidationException(
                        Map.of("url", List.of()
                )));
        when(mockValidator
                .check(any(), anyString()))
                .thenReturn(mockValidator);

        when(ctx.formParamAsClass("url", String.class)).thenReturn(mockValidator);

        UrlController.createUrl(ctx);

        verify(ctx).status(HttpStatus.BAD_REQUEST);

    }

    @Test
    public void showUrlTest() {
        Url testUrl = new Url();
        testUrl.setId(1);
        testUrl.setName("https://google.com");
        testUrl.setCreatedAt(Instant.now());

        var testUrlCheck = UrlCheck.builder()
                .urlId(1)
                .statusCode(200)
                .createdAt(Instant.now())
                .build();

        try (MockedStatic<UrlRepository> mockedUrlRepo = mockStatic(UrlRepository.class);
             MockedStatic<UrlCheckRepository> mockedUrlCheckRepo = mockStatic(UrlCheckRepository.class)
        ) {
            mockedUrlRepo.when(() -> UrlRepository.findById(1)).thenReturn(Optional.of(testUrl));
            mockedUrlCheckRepo.when(() -> UrlCheckRepository.findAllByUrlId(1))
                    .thenReturn(Optional.of(List.of(testUrlCheck)));

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
    public void checkUrlSuccess() throws IOException {
        String testHtml = Files.readString(Paths.get("src/test/resources/html/google-url-check.html"));
        System.out.println(testHtml);

        mockWebServer
                .enqueue(new MockResponse.Builder()
                .body(testHtml)
                .code(200)
                .build()
        );

        String testUrl = mockWebServer
                .url("/urls/" + 1 + "/checks")
                .toString();

        UrlCheck result = UrlCheckService.urlCheck(testUrl, 1);

        assertEquals(200, result.getStatusCode());
        assertEquals(1, result.getUrlId());
        assertEquals("Google", result.getTitle());
        assertEquals("Google h1", result.getH1());
        assertEquals("Google description", result.getDescription());
    }

    @Test
    public void buildUrlTest() {
        UrlController.buildUrl(ctx);

        verify(ctx).render(eq("urls/build.jte"), any(Map.class));
    }

    @Test
    public void showUrlsTest() {
        try (MockedStatic<UrlRepository> mockedUrlRepo = mockStatic(UrlRepository.class);
             MockedStatic<UrlCheckRepository> mockedUrlCheckRepo = mockStatic(UrlCheckRepository.class)
        ) {
            Url testUrl1 = new Url();
            testUrl1.setId(1);
            testUrl1.setName("https://google.com");
            testUrl1.setCreatedAt(Instant.now());

            Url testUrl2 = new Url();
            testUrl2.setId(2);
            testUrl2.setName("https://yandex.ru");
            testUrl2.setCreatedAt(Instant.now());

            List<Url> urlList = List.of(testUrl1, testUrl2);

            var testUrlCheck = UrlCheck.builder()
                    .urlId(1)
                    .statusCode(200)
                    .createdAt(Instant.now())
                    .build();

            mockedUrlRepo.when(UrlRepository::getAllUrls).thenReturn(Optional.of(urlList));
            mockedUrlCheckRepo.when(() -> UrlCheckRepository.findById(1))
                    .thenReturn(Optional.of(testUrlCheck));
            mockedUrlCheckRepo.when(() -> UrlCheckRepository.findById(2))
                    .thenReturn(Optional.of(UrlCheck.builder()
                            .urlId(2)
                            .statusCode(0)
                            .createdAt(Instant.now())
                            .build()));

            UrlController.showUrls(ctx);

            verify(ctx).render(eq("urls/index.jte"), any(Map.class));
            verify(ctx).consumeSessionAttribute("flash");
            mockedUrlRepo.verify(UrlRepository::getAllUrls);
        }
    }

    @Test
    public void checkUrlTest() {
        Validator<Integer> mockValidator = mock(Validator.class);
        when(mockValidator.get()).thenReturn(1);
        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(mockValidator);

        Url testUrl = new Url();
        testUrl.setId(1);
        testUrl.setName("https://google.com");
        testUrl.setCreatedAt(Instant.now());

        try (MockedStatic<UrlRepository> mockedUrlRepo = mockStatic(UrlRepository.class);
             MockedStatic<UrlCheckRepository> mockedUrlCheckRepo = mockStatic(UrlCheckRepository.class);
             MockedStatic<UrlCheckService> mockedUrlCheckService = mockStatic(UrlCheckService.class)
        ) {
            mockedUrlRepo.when(() -> UrlRepository.findById(1)).thenReturn(Optional.of(testUrl));

            var testUrlCheck = UrlCheck.builder()
                    .urlId(1)
                    .statusCode(200)
                    .title("Test Title")
                    .h1("Test H1")
                    .description("Test Description")
                    .createdAt(Instant.now())
                    .build();

            mockedUrlCheckService.when(() -> UrlCheckService.urlCheck("https://google.com", 1))
                    .thenReturn(testUrlCheck);

            UrlController.checkUrl(ctx);

            verify(ctx).redirect("/urls/1");
            mockedUrlCheckRepo.verify(() -> UrlCheckRepository.save(any(UrlCheck.class)));
        }
    }
}
