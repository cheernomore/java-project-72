package hexlet.code.model;

import lombok.Data;
import java.time.Instant;

@Data
public class Url {
    private int id;
    private String name;
    private Instant createdAt;
}
