package hexlet.code.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UrlCheck {
    private int id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private int urlId;
    private Timestamp createdAt;
}
