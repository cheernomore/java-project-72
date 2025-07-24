package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("PORT"));
        DatabaseInitializer.initializeDatabase();
        getApp().start(port);
    }

    public static Javalin getApp() {
        return Javalin.create(
                config -> config.fileRenderer(new JavalinJte(createTemplateEngine())))
                .get("/", ctx -> ctx.render("index.jte"));
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
