/*import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WebCrawlerAndInsertion {

    public static void main(String[] args) {
        String url = "https://www.mobile.bg/pcgi/mobile.cgi";

        try {
            Document document = Jsoup.connect(url)
                    .data("act", "3") // Search action
                    .data("marka", "Mercedes-Benz") // Make: Mercedes-Benz
                    .data("model", "E-класа") // Model: E-класа
                    .data("price1", "4000")  // Maximum price: 4000
                    .post();

            Elements ads = document.select(".list.bgbase");

            String dbUrl = "jdbc:mysql://localhost:3306/car_ads_db";
            String username = "root";
            String password = "databasepassword";

            try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
                String insertQuery = "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)";

                for (Element ad : ads) {
                    String make = "Mercedes-Benz"; // Set make explicitly
                    String model = "E-класа"; // Set model explicitly
                    double price = Double.parseDouble(ad.select(".price").text().replaceAll("[^\\d.]+", ""));

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setString(1, make);
                        preparedStatement.setString(2, model);
                        preparedStatement.setDouble(3, price);

                        int rowsAffected = preparedStatement.executeUpdate();
                        System.out.println(rowsAffected + " row(s) inserted.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
