package hexlet.code.service;

import hexlet.code.model.UrlCheck;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;

public class UrlCheckService {

    public static UrlCheck urlCheck(String urlName, int id) {
        try {
            var response = Unirest
                    .get(urlName)
                    .asString();

            var doc = Jsoup.parse(response.getBody());
            var statusCode = response.getStatus();

            var h1Element = doc.selectFirst("h1");
            var h1 = h1Element != null ? h1Element.text() : null;

            var titleElement = doc.selectFirst("title");
            var title = titleElement != null ? titleElement.text() : null;

            var descriptionElement = doc.selectFirst("meta[name=\"description\"]");
            var description = descriptionElement != null ? descriptionElement.attr("content") : null;

            var urlCheck = UrlCheck.builder();
            urlCheck
                    .urlId(id)
                    .h1(h1)
                    .title(title)
                    .description(description)
                    .statusCode(statusCode);

            return urlCheck.build();
        } catch (UnirestException e) {
            throw new UnirestException("Unirest Exception:", e);
        }
    }
}
