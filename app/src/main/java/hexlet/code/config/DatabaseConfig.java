package hexlet.code.config;

public final class DatabaseConfig {
    public static final String DEFAULT_H2_URL = "jdbc:h2:file:./testdb;AUTO_SERVER=TRUE";
    public static final String H2_DRIVER = "org.h2.Driver";
    public static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    public static final String DEFAULT_H2_USER = "test";
    public static final String DEFAULT_H2_PASSWORD = "";
}
