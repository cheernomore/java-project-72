package hexlet.code.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.config.DatabaseConfig;

import javax.sql.DataSource;

public class DatabaseConnection {

    private static final DataSource DATA_SOURCE = createDataSource();

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    private static DataSource createDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        String username;
        String password;

        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = DatabaseConfig.DEFAULT_H2_URL;
            username = DatabaseConfig.DEFAULT_H2_USER;
            password = DatabaseConfig.DEFAULT_H2_PASSWORD;
            hikariConfig.setDriverClassName(DatabaseConfig.H2_DRIVER);
        } else {
            username = System.getenv("JDBC_DATABASE_USERNAME");
            password = System.getenv("JDBC_DATABASE_PASSWORD");
            hikariConfig.setDriverClassName(DatabaseConfig.POSTGRES_DRIVER);
        }

        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);

        return new HikariDataSource(hikariConfig);
    }
}
