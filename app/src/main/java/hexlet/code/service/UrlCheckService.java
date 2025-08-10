package hexlet.code.service;

import hexlet.code.model.UrlCheck;
import kong.unirest.core.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class UrlCheckService {

    public static UrlCheck urlCheck(String urlName, int id) {

        var response = Unirest
                .get(urlName)
                .asString();

        var doc = Jsoup.parse(response.getBody());
        var statusCode = response.getStatus();

        var urlCheck = new UrlCheck();
        urlCheck.setStatusCode(statusCode);
        urlCheck.setH1(getElementText(doc, "h1"));
        urlCheck.setTitle(getElementText(doc, "title"));
        urlCheck.setDescription(getMetaContent(doc, "meta[name=\"description\"]"));
        urlCheck.setUrlId(id);

        return urlCheck;
    }

    private static String getElementText(Document doc, String selector) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.text().trim() : null;
    }

    private static String getMetaContent(Document doc, String selector) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.attr("content").trim() : null;
    }
}
