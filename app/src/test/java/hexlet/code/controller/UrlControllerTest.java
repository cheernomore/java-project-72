package hexlet.code.controller;

import hexlet.code.App;
import hexlet.code.db.DatabaseInitializer;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UrlControllerTest {

    private Javalin app;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initializeDatabase();
        app = App.getApp();
        app.start(0);
        io.restassured.RestAssured.port = app.port();
    }

    @AfterEach
    void tearDown() {
        app.stop();
    }

    @Test
    public void testBuildUrlPageContent() {
        given()
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .body(containsString("<form action=\"/urls\" method=\"post\">"))
                .body(containsString("<input name=\"url\" type=\"text\""))
                .body(containsString("Добавить URL"));
    }

    @Test
    public void testCreateUrlSuccessAndVerifyInDatabase() {
        var testUrl = "https://example.com";

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", testUrl)
                .when()
                .post("/urls")
                .then()
                .statusCode(302);

        assertTrue(UrlRepository.isUrlExistsByName(testUrl), "URL should be saved in database");
    }

    @Test
    public void testCreateUrlAndVerifyInUrlsList() {
        var testUrl = "https://test-display.com";

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", testUrl)
                .when()
                .post("/urls");

        given()
                .when()
                .get("/urls")
                .then()
                .statusCode(200)
                .body(containsString("https://test-display.com"))
                .body(containsString("<table class=\"table table-striped-columns\">"))
                .body(containsString("<th scope=\"col\">Urls</th>"));
    }

    @Test
    public void testUrlsListPageStructure() {
        given()
                .when()
                .get("/urls")
                .then()
                .statusCode(200)
                .body(containsString("<table class=\"table table-striped-columns\">"))
                .body(containsString("<th scope=\"col\">#</th>"))
                .body(containsString("<th scope=\"col\">Urls</th>"))
                .body(containsString("<tbody>"));
    }

    @Test
    public void testCreateUrlEmptyWithErrorDisplay() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", "")
                .when()
                .post("/urls")
                .then()
                .statusCode(200)
                .body(containsString("Url не должен быть пустым"))
                .body(containsString("<div class=\"toast align-items-center text-white bg-danger border-0 show\""))
                .body(containsString("<strong>Ошибка!</strong>"))
                .body(containsString("<form action=\"/urls\" method=\"post\">"));
    }

    @Test
    public void testCreateUrlInvalidWithErrorDisplay() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", "invalid-url")
                .when()
                .post("/urls")
                .then()
                .statusCode(200)
                .body(containsString("Некорректный URL"))
                .body(containsString("<div class=\"toast align-items-center text-white bg-danger border-0 show\""))
                .body(containsString("<strong>Ошибка!</strong>"));

        assertFalse(UrlRepository.isUrlExistsByName("invalid-url"), "Invalid URL should not be saved");
    }

    @Test
    public void testCreateUrlDuplicateWithErrorDisplay() {
        var testUrl = "https://duplicate-test.com";

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", testUrl)
                .when()
                .post("/urls");

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", testUrl)
                .when()
                .post("/urls")
                .then()
                .statusCode(200)
                .body(containsString("URL должен быть уникальным"))
                .body(containsString("<div class=\"toast align-items-center text-white bg-danger border-0 show\""))
                .body(containsString("<strong>Ошибка!</strong>"));
    }

    @Test
    public void testShowUrls() {
        given()
                .when()
                .get("/urls")
                .then()
                .statusCode(200);
    }

    @Test
    public void testShowSpecificUrlWithHtmlContent() {
        var testUrl = "https://show-specific.com";

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", testUrl)
                .when()
                .post("/urls");

        var urls = UrlRepository.getAllUrls();
        var url = urls.stream()
                .filter(u -> u.getName().equals(testUrl))
                .findFirst()
                .orElse(null);

        assertNotNull(url, "URL should exist in database");

        given()
                .when()
                .get("/urls/{id}", url.getId())
                .then()
                .statusCode(200)
                .body(containsString("URL: " + testUrl))
                .body(containsString("<div>"))
                .body(containsString("<p>URL: " + testUrl + "</p>"));
    }

    @Test
    public void testUrlTableLinkStructure() {
        var testUrl = "https://table-link-test.com";

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", testUrl)
                .when()
                .post("/urls");

        var urls = UrlRepository.getAllUrls();
        var url = urls.stream()
                .filter(u -> u.getName().equals(testUrl))
                .findFirst()
                .orElse(null);

        assertNotNull(url, "URL should exist in database");

        given()
                .when()
                .get("/urls")
                .then()
                .statusCode(200)
                .body(containsString(testUrl))
                .body(containsString("<a href=\"/urls/" + url.getId() + "\">"))
                .body(containsString("</a>"))
                .body(containsString("<th scope=\"row\">" + url.getId() + "</th>"));
    }

    @Test
    public void testMultipleUrlsDisplayInTable() {
        var url1 = "https://first-url.com";
        var url2 = "https://second-url.com";

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", url1)
                .when()
                .post("/urls");

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", url2)
                .when()
                .post("/urls");

        given()
                .when()
                .get("/urls")
                .then()
                .statusCode(200)
                .body(containsString(url1))
                .body(containsString(url2))
                .body(containsString("<tr>"));
    }

    @Test
    public void testUrlNormalization() {
        var testUrl = "https://normalize-test.com:8080/path?query=value";
        var expectedNormalized = "https://normalize-test.com:8080";

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", testUrl)
                .when()
                .post("/urls");

        var urls = UrlRepository.getAllUrls();
        var url = urls.stream()
                .filter(u -> u.getName().equals(expectedNormalized))
                .findFirst()
                .orElse(null);

        assertNotNull(url, "Normalized URL should exist in database");
        assertTrue(UrlRepository.isUrlExistsByName(expectedNormalized), "Normalized URL should be saved");
    }

    @Test
    public void testCreateUrlWithPort() {
        var testUrl = "https://example.com:3000";

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("url", testUrl)
                .when()
                .post("/urls")
                .then()
                .statusCode(302);

        assertTrue(UrlRepository.isUrlExistsByName(testUrl), "URL with port should be saved");
    }
}
