package hexlet.code.repository;

import hexlet.code.db.DatabaseConnection;
import hexlet.code.model.Url;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UrlRepository {

    public static void save(Url url) {
        if (url == null || url.getName() == null) {
            throw new IllegalArgumentException("URL and URL name cannot be null");
        }

        String sql = "INSERT INTO urls(name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, url.getName());
            stmt.executeUpdate();
            log.info("Successfully saved URL: {}", url.getName());
        } catch (SQLException e) {
            log.error("Failed to save URL: {}", url.getName(), e);
            throw new RuntimeException("Failed to save URL to database", e);
        }
    }

    public static List<Url> getAllUrls() {
        var urls = new ArrayList<Url>();
        var sql = "SELECT * FROM urls";

        try (Connection conn = DatabaseConnection.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Url url = new Url();
                url.setName(resultSet.getString("name"));
                url.setId(resultSet.getInt("id"));

                urls.add(url);
            }
            log.info("Successfully get urls: ");
            return urls;
        } catch (SQLException e) {
            log.error("Failed to get urls: ", e);
            throw new RuntimeException("Failed to get urls", e);
        }
    }

    public static Url findById(int id) {
        var sql =
            """
                SELECT * FROM urls WHERE urls.id = ?
            """;

        try (Connection conn = DatabaseConnection.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Url url = new Url();
                url.setId(resultSet.getInt("id"));
                url.setName(resultSet.getString("name"));

                log.info("Successfully found url with id: {}", id);
                return url;
            } else {
                log.info("No url found with id: {}", id);
                return null;
            }
        } catch (SQLException e) {
            log.error("Failed to get url: ", e);
            throw new RuntimeException("Failed to get url", e);
        }
    }

    public static boolean isUrlExistsByName(String urlName) {
        var sql =
                """
                    SELECT EXISTS(SELECT * FROM urls WHERE urls.name = ?)
                """;

        try (
                Connection conn = DatabaseConnection.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ) {
            stmt.setString(1, urlName);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;
        } catch (SQLException e) {
            log.error("Ошибка при проверке существования URL: {}", urlName, e);
            throw new RuntimeException("Ошибка при работе с базой данных");
        }
    }
}
