import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CarsBgCrawler {

    public static void main(String[] args) {
        String url = "https://www.cars.bg/carslist.php?subm=1&add_search=1&typeoffer=1&brandId=54&models%5B%5D=1000018&priceTo=4000";

        String dbUrl = "jdbc:mysql://localhost:3306/car_ads_db"; // Change to your database URL
        String dbUser = "root";
        String dbPassword = "databasepassword";

        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            Document document = Jsoup.connect(url).get();
            Elements offerItems = document.select(".mdc-card.offer-item");

            for (Element offerItem : offerItems) {
                String title = offerItem.select(".card__title.observable").text();
                String price = offerItem.select(".card__title.price").text();
                String make = "Mercedes-Benz"; // Since we're targeting Mercedes-Benz
                String model = title.substring(title.indexOf("Mercedes-Benz") + 14); // Extract the model from the title

                insertAdData(connection, make, model, price);
            }

            connection.close();
            System.out.println("Ads inserted into the database successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertAdData(Connection connection, String make, String model, String price) throws SQLException {
        System.out.println("Price before cleaning: " + price);

        // Clean up price value by removing non-numeric and non-dot characters
        String cleanedPrice = price.replaceAll("[^0-9.]", "");

        System.out.println("Price after cleaning: " + cleanedPrice);

        if (!cleanedPrice.isEmpty()) {
            String insertQuery = "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, make);
                preparedStatement.setString(2, model);
                preparedStatement.setString(3, cleanedPrice);
                preparedStatement.executeUpdate();
            }
        } else {
            System.out.println("Skipping empty price.");
        }
    }


}
