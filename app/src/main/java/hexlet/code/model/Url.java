package hexlet.code.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Url {
    private int id;
    private String name;
    private Timestamp createdAt;
}
