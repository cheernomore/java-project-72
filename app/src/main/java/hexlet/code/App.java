package hexlet.code;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("PORT"));
        DatabaseInitializer.initializeDatabase();
        getApp().start(port);
    }

    public static Javalin getApp() {
        return Javalin.create()
                .get("/", ctx -> ctx.result("Hello World"));
    }
}
