package hexlet.code.service;

import hexlet.code.model.UrlCheck;
import kong.unirest.core.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class UrlCheckService {

    public static UrlCheck urlCheck(String urlName, int id) {

        var response = Unirest
                .get(urlName)
                .asString();

        var doc = Jsoup.parse(response.getBody());
        var statusCode = response.getStatus();

        var urlCheck = new UrlCheck();
        urlCheck.setStatusCode(statusCode);
        urlCheck.setH1(getElementValueOrNull(doc, "h1"));
        urlCheck.setTitle(getElementValueOrNull(doc, "title"));
        urlCheck.setDescription(getElementValueOrNull(doc, "description"));
        urlCheck.setUrlId(id);

        return urlCheck;
    }

    private static String getElementValueOrNull(Document doc, String elementName) {
        return doc.selectFirst(elementName) == null ? "" : doc.selectFirst(elementName).text();
    }
}
