import Utilities.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.sql.*;
import java.util.Properties;

public class MobileBgWebCrawler {

    public static void main(String[] args) {
        Properties properties = DatabaseUtility.loadDatabaseProperties("C:\\Users\\sepre\\OneDrive\\Desktop\\JavaProjects\\CrawlingMobileBG\\src\\main\\resources\\config.properties");
        System.out.println("Starting data scraping and storing process...");
        int totalPages = WebUtility.getTotalPages("https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=tehuex&f1=1");
        System.out.println("Total pages to process: " + totalPages);

        int numThreads = 5;
        ThreadManager threadManager = new ThreadManager(numThreads);

        for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
            String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=tehuex&f1=" + currentPage;

            threadManager.executeTask(() -> {
                System.out.println("Processing page: " + url);
                processPage(url, properties);
            });
        }

        threadManager.shutdown();

        System.out.println("Data scraping and storing completed successfully.");
    }

    private static void processPage(String url, Properties properties) {
        Document document;
        try {
            document = Jsoup.connect(url).execute().charset("UTF-8").parse();
        } catch (IOException e) {
            System.err.println("Error connecting to URL: " + e.getMessage());
            return;
        }

        List<AdListing> adListings = PageScraper.scrapeAdsFromPage(document);

        try (Connection connection = DatabaseUtility.getConnection(properties)) {
            String sql = "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (AdListing adListing : adListings) {
                    String[] titleParts = adListing.title().split(" ", 2);
                    String make = titleParts[0];
                    String model = titleParts.length > 1 ? titleParts[1] : "";

                    statement.setString(1, make);
                    statement.setString(2, model);
                    statement.setString(3, adListing.price());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
