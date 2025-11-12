package hexlet.code.controller;

import hexlet.code.dto.urls.UrlBuildPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.UrlCheckService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.rendering.template.TemplateUtil.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.HashMap;

@Slf4j
public class UrlController {

    public static void buildUrl(Context ctx) {
        UrlBuildPage urlBuildPage = new UrlBuildPage();
        ctx.render("urls/build.jte", model("page", urlBuildPage));
    }

    public static void createUrl(Context ctx) {
        UrlBuildPage urlBuildPage = new UrlBuildPage();
        String inputUrl;
        URL parsedUrl;

        try {
            inputUrl = ctx.formParamAsClass("url", String.class)
                    .check(value -> !value.isBlank(), "Url не должен быть пустым")
                    .check(value -> !UrlRepository.isUrlExistsByName(value), "URL должен быть уникальным")
                    .get();
        } catch (ValidationException e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            log.error("Url заполнен не корректно", e);

            urlBuildPage.setErrors(e.getErrors());
            urlBuildPage.setFlash("Ошибка!");

            ctx.render("urls/build.jte", model("page", urlBuildPage));
            return;
        }

        try {
            parsedUrl = URI.create(inputUrl).toURL();

            Url url = new Url();
            url.setName(normalizeUrl(parsedUrl));
            UrlRepository.save(url);

            ctx.sessionAttribute("flash", "Url has been created!");
            ctx.status(HttpStatus.CREATED);
            ctx.redirect(NamedRoutes.urlsPath());

        } catch (MalformedURLException e) {
            log.error("Url is not correct: {}", inputUrl, e);

            ctx.sessionAttribute("flash", inputUrl + " is not correct");
            ctx.render("urls/build.jte", model("page", urlBuildPage));
        }
    }

    public static void show(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.findById(id).orElseThrow();

        var urlChecks = UrlCheckRepository.findAllByUrlId(id).orElseThrow();

        var page = new UrlPage(url, urlChecks);

        ctx.render("urls/show.jte", model("page", page));
    }

    public static void showUrls(Context ctx) {
        var urlChecks = new HashMap<Integer, UrlCheck>();
        var urls = UrlRepository.getAllUrls().orElseThrow();

        urls.forEach(url -> urlChecks.put(url.getId(), UrlCheckRepository.findById(url.getId()).orElseThrow()));

        var page = new UrlsPage(urls, urlChecks);

        String flash = ctx.consumeSessionAttribute("flash");
        page.setFlash(flash);

        ctx.render("urls/index.jte", model("page", page));
    }

    public static void checkUrl(Context ctx) {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.findById(id).orElseThrow();

        System.out.println();
        var urlCheck = UrlCheckService.urlCheck(url.getName(), url.getId());

        UrlCheckRepository.save(urlCheck);
        ctx.redirect(NamedRoutes.urlPath(String.valueOf(id)));

    }

    private static String normalizeUrl(URL url) {
        String result = url.getProtocol() + "://" + url.getHost();
        if (url.getPort() != -1) {
            result += ":" + url.getPort();
        }
        return result;
    }
}
