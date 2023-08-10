/*import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MobileBgCrawler {

    public static void main(String[] args) {
        String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=td61bn&f1=1";

        String dbUrl = "jdbc:mysql://localhost:3306/car_ads_db";
        String dbUser = "root";
        String dbPassword = "databasepassword";

        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            Document document = Jsoup.connect(url).get();
            Elements offerItems = document.select(".mmm"); // Selecting ad title elements

            for (Element offerItem : offerItems) {
                Element titleElement = offerItem.selectFirst("a.mmm");
                String title = titleElement.text();

                Element priceElement = offerItem.selectFirst(".price");
                String price = priceElement.text();

                String make = "Mercedes-Benz"; // Since we're targeting Mercedes-Benz

                int indexOfHyphen = title.indexOf("-");
                if (indexOfHyphen != -1) {
                    String model = title.substring(indexOfHyphen + 1).trim();

                    System.out.println("Title: " + title);
                    System.out.println("Price: " + price);
                    System.out.println("Model: " + model);

                    insertAdData(connection, make, model, price);
                } else {
                    System.out.println("Hyphen not found in title: " + title);
                }
            }

            connection.close();
            System.out.println("Ads inserted into the database successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertAdData(Connection connection, String make, String model, String price) throws SQLException {
        System.out.println("Price extracted from ad: " + price);

        String insertQuery = "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, make);
            preparedStatement.setString(2, model);
            preparedStatement.setString(3, price);
            preparedStatement.executeUpdate();
        }
    }
}
*/