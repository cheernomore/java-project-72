package hexlet.code.controller;

import hexlet.code.dto.urls.UrlBuildPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.validation.ValidationException;

import static io.javalin.rendering.template.TemplateUtil.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.Objects;

public class UrlController {

    public static void buildUrl(Context ctx) {
        var page = new UrlBuildPage();
        ctx.render("urls/build.jte", model("page", page));
    }

    public static void createUrl(Context ctx) {

        try {
            var urlName = ctx.formParamAsClass("url", String.class)
                    .check(value -> !value.isEmpty(), "Url не должен быть пустым")
                    .check(UrlController::isValidUrl, "Некорректный URL")
                    .check(UrlController::isUnique, "URL должен быть уникальным")
                    .get();

            var parsedUrl = URI.create(urlName).toURL();

            var url = new Url();
            url.setName(normalizeUrl(parsedUrl));
            UrlRepository.save(url);

            ctx.sessionAttribute("flash", "Url has been created!");
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (ValidationException e) {
            var urlName = ctx.formParam("url");
            var page = new UrlBuildPage(urlName, e.getErrors());

            ctx.render("urls/build.jte", model("page", page));
        } catch (MalformedURLException e) {
            var urlName = ctx.formParam("url");
            var page = new UrlBuildPage();

            ctx.sessionAttribute("flash", urlName + " not correct");

            ctx.render("urls/build.jte", model("page", page));
        }
    }

    public static void show(Context ctx) {
        var id = ctx.pathParamAsClass("{id}", Integer.class).get();
        var url = UrlRepository.findById(id);

        var page = new UrlPage(url);

        ctx.render("urls/show.jte", model("page", page));
    }

    public static void showUrls(Context ctx) {
        var urls = UrlRepository.getAllUrls();
        var page = new UrlsPage(urls);

        String flash = ctx.consumeSessionAttribute("flash");
        page.setFlash(flash);

        ctx.render("urls/index.jte", model("page", page));
    }

    private static boolean isValidUrl(String url) {
        try {
            URI.create(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String normalizeUrl(URL url) {
        String result = url.getProtocol() + "://" + url.getHost();
        if (url.getPort() != -1) {
            result += ":" + url.getPort();
        }
        return result;
    }

    private static boolean isUnique(String urlName) {
        return !UrlRepository.isUrlExistsByName(urlName);
    }
}