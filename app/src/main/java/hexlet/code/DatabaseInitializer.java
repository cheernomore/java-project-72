package hexlet.code;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection connection = DatabaseConnection.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {

            InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql");
            if (inputStream == null) {
                throw new RuntimeException("Файл schema.sql не найден!");
            }

            Scanner scanner = new Scanner(inputStream).useDelimiter(";");
            while (scanner.hasNext()) {
                String sql = scanner.next().trim();
                if (!sql.isEmpty()) {
                    statement.execute(sql);
                }
            }
            System.out.println("База данных инициализирована.");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при инициализации БД", e);
        }
    }
}
