package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.NamedRoutes;
import hexlet.code.controller.UrlController;
import hexlet.code.db.DatabaseInitializer;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        int port = getPort();
        DatabaseInitializer.initializeDatabase();
        getApp().start(port);
    }

    public static Javalin getApp() {
        return Javalin.create(
                config -> config.fileRenderer(new JavalinJte(createTemplateEngine()))
                )
                .get("/", UrlController::buildUrl)
                .get(NamedRoutes.urlsPath(), UrlController::showUrls)
                .get(NamedRoutes.buildUrlPath(), UrlController::buildUrl)
                .get(NamedRoutes.urlPath("{id}"), UrlController::show)
                .post(NamedRoutes.urlsPath(), UrlController::createUrl)
                .post(NamedRoutes.urlsIdCheck("{id}"), UrlController::checkUrl);
    }

    private static int getPort() {
        String portEnv = System.getenv("PORT");
        return portEnv != null ? Integer.parseInt(portEnv) : 8080;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
