/*import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SimpleWebCrawlerWithDatabase {

    public static void main(String[] args) {
        String url = "https://mobile.bg";

        try {
            Document document = Jsoup.connect(url).get();

            String title = document.title();
            System.out.println("Title: " + title);

            // Retrieve the car ad data (example values)
            String make = "Toyota";
            String model = "Corolla";
            double price = 15000.00;

            // Establish a JDBC connection to your MySQL database
            String dbUrl = "jdbc:mysql://localhost:3306/car_ads_db";
            String username = "root";
            String password = "databasepassword";

            try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
                // Create an SQL INSERT statement
                String insertQuery = "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)";

                // Create a PreparedStatement and set parameter values
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, make);
                    preparedStatement.setString(2, model);
                    preparedStatement.setDouble(3, price);

                    // Execute the INSERT statement
                    int rowsAffected = preparedStatement.executeUpdate();
                    System.out.println(rowsAffected + " row(s) inserted.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String htmlContent = document.html();
            System.out.println("HTML Content:\n" + htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
