package Utilities;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtility {

    public static Properties loadDatabasePropertiesFromClasspath(String filename) {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseUtility.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new IOException("File not found in classpath: " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static Connection getConnection(Properties properties) throws SQLException {
        String dbUrl = properties.getProperty("dbUrl");
        String dbUser = properties.getProperty("dbUser");
        String dbPassword = properties.getProperty("dbPassword");
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
