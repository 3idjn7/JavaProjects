import Utilities.DatabaseUtility;
import Utilities.WebUtility;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.sql.*;
import java.util.Properties;

public class MobileBgWebCrawler {

    public static void main(String[] args) {
        Properties properties = DatabaseUtility.loadDatabaseProperties("C:\\Users\\sepre\\OneDrive\\Desktop\\JavaProjects\\CrawlingMobileBG\\src\\main\\resources\\config.properties");

        int totalPages = WebUtility.getTotalPages("https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=td61bn&f1=1");

        for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
            String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=td61bn&f1=" + currentPage;

            Document document;
            try {
                document = Jsoup.connect(url).execute().charset("UTF-8").parse();
            } catch (IOException e) {
                System.err.println("Error connecting to URL: " + e.getMessage());
                continue;
            }

            List<AdListing> adListings = PageScraper.scrapeAdsFromPage(document);

            try (Connection connection = DatabaseUtility.getConnection(properties)) {
                String sql = "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    for (AdListing adListing : adListings) {
                        String[] titleParts = adListing.getTitle().split(" ", 2);
                        String make = titleParts[0];
                        String model = titleParts.length > 1 ? titleParts[1] : "";

                        statement.setString(1, make);
                        statement.setString(2, model);
                        statement.setString(3, adListing.getPrice());
                        statement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Data scraping and storing completed successfully.");
    }
}
