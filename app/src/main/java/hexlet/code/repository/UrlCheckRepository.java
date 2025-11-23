package hexlet.code.repository;

import hexlet.code.db.DatabaseConnection;
import hexlet.code.model.UrlCheck;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class UrlCheckRepository {

    public static void save(UrlCheck urlCheck) {
        if (urlCheck == null) {
            throw new IllegalArgumentException("UrlCheck cannot be null");
        }

        var sql =
                """
                INSERT INTO url_checks(status_code, title, h1, description, url_id, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        try (
                Connection connection = DatabaseConnection.getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
                ) {

            stmt.setInt(1, urlCheck.getStatusCode());
            stmt.setString(2, urlCheck.getTitle());
            stmt.setString(3, urlCheck.getH1());
            stmt.setString(4, urlCheck.getDescription());
            stmt.setInt(5, urlCheck.getUrlId());
            stmt.setTimestamp(6, timestamp);

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
                var urlCheck = UrlCheck.builder();

                urlCheck.urlId(rs.getInt("id"))
                        .statusCode(rs.getInt("status_code"))
                        .h1(rs.getString("h1"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .createdAt(rs.getTimestamp("created_at").toInstant())
                        .build();

                urlChecks.add(urlCheck.build());
            }

            return urlChecks;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get url checks", e);
        }
    }

    public static Optional<UrlCheck> findById(int id) {
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
            var urlCheck = UrlCheck.builder();
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                urlCheck.urlId(rs.getInt("id"))
                        .statusCode(rs.getInt("status_code"))
                        .h1(rs.getString("h1"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .createdAt(rs.getTimestamp("created_at").toInstant())
                        .build();
            }

            return Optional.of(urlCheck.build());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, UrlCheck> findLatestChecks() {
        var latestChecks = new HashMap<Integer, UrlCheck>();
        var sql =
                """
                SELECT uc.*
                FROM url_checks uc
                INNER JOIN (
                    SELECT url_id, MAX(created_at) as max_created_at
                    FROM url_checks
                    GROUP BY url_id
                ) latest ON uc.url_id = latest.url_id AND uc.created_at = latest.max_created_at
                """;

        try (
                Connection connection = DatabaseConnection.getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            var rs = stmt.executeQuery();

            while (rs.next()) {
                var urlCheck = UrlCheck.builder()
                        .urlId(rs.getInt("id"))
                        .statusCode(rs.getInt("status_code"))
                        .h1(rs.getString("h1"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .createdAt(rs.getTimestamp("created_at").toInstant())
                        .build();

                latestChecks.put(rs.getInt("url_id"), urlCheck);
            }

            return latestChecks;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get latest url checks", e);
        }
    }
}
