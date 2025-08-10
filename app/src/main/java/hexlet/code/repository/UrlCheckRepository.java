package hexlet.code.repository;

import hexlet.code.db.DatabaseConnection;
import hexlet.code.model.UrlCheck;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UrlCheckRepository {

    public static void save(UrlCheck urlCheck) {
        if (urlCheck == null) {
            throw new IllegalArgumentException("UrlCheck cannot be null");
        }

        var sql =
                """
                INSERT INTO url_checks(status_code, title, h1, description, url_id, created_at)
                VALUES (?, ?, ?, ?, ?, DEFAULT)
                """;

        try (
                Connection connection = DatabaseConnection.getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
                ) {

            stmt.setInt(1, urlCheck.getStatusCode());
            stmt.setString(2, urlCheck.getTitle());
            stmt.setString(3, urlCheck.getH1());
            stmt.setString(4, urlCheck.getDescription());
            stmt.setInt(5, urlCheck.getUrlId());

            stmt.executeUpdate();
            log.info("Successfully saved UrlCheck with title: {}", urlCheck.getTitle());
        } catch (SQLException e) {
            log.error("Failed to save UrlCheck to database with title: {}", urlCheck.getTitle());
            throw new RuntimeException("Failed to save UrlCheck to database", e);
        }
    }

    public static List<UrlCheck> findAllByUrlId(int id) {
        var urlChecks = new ArrayList<UrlCheck>();
        var sql =
                """
                SELECT *
                FROM url_checks
                WHERE url_id = ?
                """;
        try (
                Connection connection = DatabaseConnection.getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();

            while (rs.next()) {
                var urlCheck = new UrlCheck();

                urlCheck.setId(rs.getInt("id"));
                urlCheck.setStatusCode(rs.getInt("status_code"));
                urlCheck.setH1(rs.getString("h1"));
                urlCheck.setTitle(rs.getString("title"));
                urlCheck.setDescription(rs.getString("description"));
                urlCheck.setCreatedAt(rs.getTimestamp("created_at"));

                urlChecks.add(urlCheck);
            }

            return urlChecks;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get url checks", e);
        }
    }

    public static UrlCheck findById(int id) {
        var urlCheck = new UrlCheck();
        var sql =
                """
                    SELECT *
                    FROM url_checks
                    WHERE url_id = ?
                    ORDER BY created_at DESC
                """;

        try (
                Connection connection = DatabaseConnection.getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                urlCheck.setId(rs.getInt("id"));
                urlCheck.setStatusCode(rs.getInt("status_code"));
                urlCheck.setH1(rs.getString("h1"));
                urlCheck.setTitle(rs.getString("title"));
                urlCheck.setDescription(rs.getString("description"));
                urlCheck.setCreatedAt(rs.getTimestamp("created_at"));
            }

            return urlCheck;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
