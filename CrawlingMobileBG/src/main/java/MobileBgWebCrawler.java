import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MobileBgWebCrawler {

    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3306/car_ads_db";
        String dbUser = "root";
        String dbPassword = "databasepassword";

        try {
            int totalPages = getTotalPages();

            for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
                String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=td61bn&f1=" + currentPage;

                Document document = Jsoup.connect(url).execute().charset("UTF-8").parse();
                List<AdListing> adListings = PageScraper.scrapeAdsFromPage(document);

                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getTotalPages() {
        String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=td61bn&f1=1";

        try {
            Document document = Jsoup.connect(url).get();
            Element totalPagesInfo = document.selectFirst("span.pageNumbersInfo b");

            if (totalPagesInfo != null) {
                String totalPagesText = totalPagesInfo.text()
                        .replaceAll("[^0-9]+", ""); // Remove non-digit characters

                try {
                    return Integer.parseInt(totalPagesText);
                } catch (NumberFormatException e) {
                    System.err.println("Error converting total pages text to integer: " + totalPagesText);
                }
            } else {
                System.err.println("Total pages info element not found.");
            }
        } catch (IOException e) {
            System.err.println("Error connecting to URL: " + e.getMessage());
        }

        // Return a default value or throw an exception depending on your needs
        return 0;
    }
}
