/*import org.jsoup.Jsoup;
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
        String url = "https://www.cars.bg/carslist.php?subm=1&add_search=1&typeoffer=1&brandId=54&models%5B%5D=1000018&models%5B%5D=874&models%5B%5D=875&models%5B%5D=876&models%5B%5D=877&models%5B%5D=878&models%5B%5D=879&models%5B%5D=880&models%5B%5D=881&models%5B%5D=882&models%5B%5D=883&models%5B%5D=884&models%5B%5D=885&models%5B%5D=886&models%5B%5D=887&models%5B%5D=888&models%5B%5D=2084&models%5B%5D=889&models%5B%5D=2303&models%5B%5D=890&models%5B%5D=891&models%5B%5D=2215&models%5B%5D=892&models%5B%5D=894&conditions%5B%5D=4&conditions%5B%5D=1";

        String dbUrl = "jdbc:mysql://localhost:3306/car_ads_db";
        String dbUser = "root";
        String dbPassword = "databasepassword";

        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            Document document = Jsoup.connect(url).get();
            Elements offerItems = document.select(".mdc-card.offer-item");

            for (Element offerItem : offerItems) {
                String title = offerItem.select(".card__title.observable").text();
                String price = offerItem.select(".card__title.mdc-typography.mdc-typography--headline6.price").text();
                String make = "Mercedes-Benz";
                String model = title.substring(title.indexOf("Mercedes-Benz") + 14);
                String dateText = offerItem.select(".card__subtitle.mdc-typography.mdc-typography--overline").text();
                String[] dateParts = dateText.split(",");
                String dateString = dateParts[1].trim();

                System.out.println("Title: " + title);
                System.out.println("Price: " + price);
                System.out.println("Model: " + model);

                insertAdData(connection, make, model, price, dateString);
            }



            connection.close();
            System.out.println("Ads inserted into the database successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertAdData(Connection connection, String make, String model, String price, String timeString) throws SQLException {
        System.out.println("Price extracted from ad: " + price);

        String insertQuery = "INSERT INTO car_ads (make, model, price, time_added) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, make);
            preparedStatement.setString(2, model);
            preparedStatement.setString(3, price);
            preparedStatement.setString(4, timeString);
            preparedStatement.executeUpdate();
        }
    }
}*/
