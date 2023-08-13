package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtility {

    public static Properties loadDatabaseProperties(String propertiesFilePath) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
            properties.load(fis);
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
