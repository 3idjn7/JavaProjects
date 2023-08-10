import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WebCrawler {

    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3306/car_ads_db";
        String dbUser = "root";
        String dbPassword = "databasepassword";

        try {
            String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=td61bn&f1=2";

            Document document = Jsoup.connect(url).get();
            Elements adElements = document.select("table.tablereset");

            for (Element adElement : adElements) {
                Element titleElement = adElement.selectFirst("td.valgtop a.mmm");
                Element priceElement = adElement.selectFirst("td.algright.valgtop span.price");

                if (titleElement != null && priceElement != null) {
                    String title = titleElement.text();
                    String price = priceElement.text();

                    // Extract make and model from the title
                    String[] titleParts = title.split(" ", 2);
                    String make = titleParts[0];
                    String model = titleParts.length > 1 ? titleParts[1] : "";

                    // Store data in the database
                    try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                        String sql = "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)";
                        try (PreparedStatement statement = connection.prepareStatement(sql)) {
                            statement.setString(1, make);
                            statement.setString(2, model);
                            statement.setString(3, price);
                            statement.executeUpdate();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Data scraping and storing completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
