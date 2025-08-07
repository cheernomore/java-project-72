package hexlet.code.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.config.DatabaseConfig;

import javax.sql.DataSource;

public class DatabaseConnection {

    public static DataSource getDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        String username;
        String password;

        if (jdbcUrl.isEmpty()) {
            jdbcUrl = DatabaseConfig.DEFAULT_H2_URL;
            username = DatabaseConfig.DEFAULT_H2_USER;
            password = DatabaseConfig.DEFAULT_H2_PASSWORD;
            hikariConfig.setDriverClassName(DatabaseConfig.H2_DRIVER);
        } else {
            username = System.getenv("JDBC_DATABASE_USERNAME");
            password = System.getenv("JDBC_DATABASE_PASSWORD");
        }

        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(DatabaseConfig.POSTGRES_DRIVER);

        return new HikariDataSource(hikariConfig);
    }
}
