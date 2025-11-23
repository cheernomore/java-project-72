package hexlet.code.controller;

import hexlet.code.dto.urls.UrlBuildPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.UrlCheckService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.rendering.template.TemplateUtil.model;

import java.net.URI;

@Slf4j
public class UrlController {

    public static void buildUrl(Context ctx) {
        UrlBuildPage urlBuildPage = new UrlBuildPage();
        ctx.render("urls/build.jte", model("page", urlBuildPage));
    }

    public static void createUrl(Context ctx) {
        var inputUrl = ctx.formParam("url");

        if (inputUrl == null || inputUrl.isBlank()) {
            ctx.sessionAttribute("flash", "URL не должен быть пустым");
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.redirect(NamedRoutes.buildUrlPath());
            return;
        }

        URI parsedUrl;
        try {
            parsedUrl = new URI(inputUrl);
        } catch (Exception e) {
            log.error("Некорректный URL: {}", inputUrl, e);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.redirect(NamedRoutes.buildUrlPath());
            return;
        }

        String normalizedUrl = normalizeUrl(parsedUrl);

        if (UrlRepository.isUrlExistsByName(normalizedUrl)) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect(NamedRoutes.urlsPath());
            return;
        }

        Url url = new Url();
        url.setName(normalizedUrl);
        UrlRepository.save(url);

        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.status(HttpStatus.CREATED);
        ctx.redirect(NamedRoutes.urlsPath());
    }

    public static void show(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.findById(id).orElseThrow();

        var urlChecks = UrlCheckRepository.findAllByUrlId(id);

        var page = new UrlPage(url, urlChecks);

        ctx.render("urls/show.jte", model("page", page));
    }

    public static void showUrls(Context ctx) {
        var urls = UrlRepository.getAllUrls();
        var urlChecks = UrlCheckRepository.findLatestChecks();

        var page = new UrlsPage(urls, urlChecks);

        String flash = ctx.consumeSessionAttribute("flash");
        page.setFlash(flash);

        ctx.render("urls/index.jte", model("page", page));
    }

    public static void checkUrl(Context ctx) {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.findById(id).orElseThrow();

        try {
            var urlCheck = UrlCheckService.urlCheck(url.getName(), url.getId());
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
        } catch (Exception e) {
            log.error("Ошибка при проверке URL: {}", url.getName(), e);
            ctx.sessionAttribute("flash", "Некорректный адрес");
        }

        ctx.redirect(NamedRoutes.urlPath(String.valueOf(id)));
    }

    private static String normalizeUrl(URI uri) {
        String result = uri.getScheme() + "://" + uri.getHost();
        if (uri.getPort() != -1) {
            result += ":" + uri.getPort();
        }
        return result;
    }
}
