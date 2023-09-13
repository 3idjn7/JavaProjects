import Utilities.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MobileBgAdUpdater {

    private static final AtomicInteger totalAdsProcessed = new AtomicInteger(0);

    public static void main(String[] args) {
        Properties properties = DatabaseUtility.loadDatabasePropertiesFromClasspath("config.properties");
        System.out.println("Starting ad checking and updating process...");
        int newAdsAdded = checkAndUpdateAds(properties);
        System.out.println("Ad checking and updating process completed.");
        System.out.println("Number of new ads added: " + newAdsAdded);
        System.out.println("Total ads processed: " + totalAdsProcessed);
    }

    public static int checkAndUpdateAds(Properties properties) {
        System.out.println("Fetching existing ads from the database...");
        int totalPages = WebUtility.getTotalPages();
        System.out.println("Total pages to process: " + totalPages);

        AtomicInteger newAdsAdded = new AtomicInteger(); // Counter for new ads added

        WebUtility.processPages(totalPages, url -> ThreadManager.getInstance().executeTask(() -> {
            System.out.println("Processing page: " + url);
            int adsAdded = processPage(url, properties);
            synchronized (MobileBgAdUpdater.class) {
                newAdsAdded.addAndGet(adsAdded);
            }
        }));

        ThreadManager.shutdown();

        System.out.println("Checking for new ads and updating the database completed.");
        return newAdsAdded.get(); // Return the total count of new ads added
    }

    private static int processPage(String url, Properties properties) {
        Document document;
        int adsAdded = 0; // Counter for new ads added

        try {
            document = Jsoup.connect(url).execute().charset("UTF-8").parse();
        } catch (IOException e) {
            System.err.println("Error connecting to URL: " + e.getMessage());
            return adsAdded;
        }

        List<AdListing> newAdListings = PageScraper.scrapeAdsFromPage(document);

        if (!newAdListings.isEmpty()) {
            try (Connection connection = DatabaseUtility.getConnection(properties)) {
                String sql = "INSERT INTO car_ads (make, model, price, new_flag) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    for (AdListing adListing : newAdListings) {
                        String[] titleParts = adListing.title().split(" ", 2);
                        String make = titleParts[0];
                        String model = titleParts.length > 1 ? titleParts[1] : "";
                        totalAdsProcessed.incrementAndGet();

                        if (!isAdExistsInDatabase(connection, make, model)) {
                            statement.setString(1, make);
                            statement.setString(2, model);
                            statement.setString(3, adListing.price());
                            statement.setBoolean(4, true);
                            statement.executeUpdate();
                            adsAdded++; // Increment the counter for new ads added
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return adsAdded; // Return the count of new ads added for this page
    }

    private static boolean isAdExistsInDatabase(Connection connection, String make, String model) {
        String sql = "SELECT COUNT(*) FROM car_ads WHERE make = ? AND model = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, make);
            statement.setString(2, model);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
