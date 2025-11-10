package hexlet.code.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UrlCheck {
    private int id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private int urlId;
    private Instant createdAt;
}
