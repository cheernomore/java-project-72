package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConnection {
    private static final String DEFAULT_H2_URL = "jdbc:h2:mem:project"; //DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:schema.sql'";
    private static final String DEFAULT_H2_USER = "test";
    private static final String DEFAULT_H2_PASSWORD = "";

    public static DataSource getDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        String username;
        String password;

        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = DEFAULT_H2_URL;
            username = DEFAULT_H2_USER;
            password = DEFAULT_H2_PASSWORD;
        } else {
            username = System.getenv("JDBC_DATABASE_USERNAME");
            password = System.getenv("JDBC_DATABASE_PASSWORD");
        }

        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        return new HikariDataSource(hikariConfig);
    }
}