package hexlet.code.controller;

public class NamedRoutes {
    public static String urlsPath() {
        return "/urls";
    }

    public static String buildUrlPath() {
        return "/urls/build";
    }

    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    public static String handleUrlPath(Integer id) {
        return urlPath(String.valueOf(id));
    }
}
