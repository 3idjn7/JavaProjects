import Utilities.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MobileBgAdUpdater {

    public static void main(String[] args) {
        Properties properties = DatabaseUtility.loadDatabaseProperties("C:\\Users\\sepre\\OneDrive\\Desktop\\JavaProjects\\CrawlingMobileBG\\src\\main\\resources\\config.properties");
        System.out.println("Starting ad checking and updating process...");
        int newAdsAdded = checkAndUpdateAds(properties);
        System.out.println("Ad checking and updating process completed.");
        System.out.println("Number of new ads added: " + newAdsAdded);
    }

    public static int checkAndUpdateAds(Properties properties) {
        System.out.println("Fetching existing ads from the database...");
        Set<String> existingAds = getExistingAdsFromDatabase(properties);

        int totalPages = WebUtility.getTotalPages("https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=tehuex&f1=1");
        System.out.println("Total pages to process: " + totalPages);

        int numThreads = 5;
        AtomicInteger newAdsAdded = new AtomicInteger(); // Counter for new ads added

        ThreadManager threadManager = new ThreadManager(numThreads);

        for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
            String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=tehuex&f1=" + currentPage;

            threadManager.executeTask(() -> {
                System.out.println("Processing page: " + url);
                int adsAdded = processPage(url, properties, existingAds);
                synchronized (MobileBgAdUpdater.class) {
                    newAdsAdded.addAndGet(adsAdded);
                }
            });
        }

        threadManager.shutdown();

        System.out.println("Checking for new ads and updating the database completed.");
        return newAdsAdded.get(); // Return the total count of new ads added
    }

    private static int processPage(String url, Properties properties, Set<String> existingAds) {
        Document document;
        int adsAdded = 0; // Counter for new ads added

        try {
            document = Jsoup.connect(url).execute().charset("UTF-8").parse();
        } catch (IOException e) {
            System.err.println("Error connecting to URL: " + e.getMessage());
            return adsAdded;
        }

        List<AdListing> newAdListings = PageScraper.scrapeAdsFromPage(document);
        List<AdListing> adsToAdd = new ArrayList<>();

        for (AdListing newAd : newAdListings) {
            if (!existingAds.contains(newAd.title())) {
                adsToAdd.add(newAd);
                existingAds.add(newAd.title());
                adsAdded++; // Increment the counter for new ads added
            }
        }

        if (!adsToAdd.isEmpty()) {
            try (Connection connection = DatabaseUtility.getConnection(properties)) {
                String sql = "INSERT INTO car_ads (make, model, price, new_flag) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    for (AdListing adListing : adsToAdd) {
                        String[] titleParts = adListing.title().split(" ", 2);
                        String make = titleParts[0];
                        String model = titleParts.length > 1 ? titleParts[1] : "";

                        statement.setString(1, make);
                        statement.setString(2, model);
                        statement.setString(3, adListing.price());
                        statement.setBoolean(4, true);
                        statement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return adsAdded; // Return the count of new ads added for this page
    }

    private static Set<String> getExistingAdsFromDatabase(Properties properties) {
        Set<String> existingAds = new HashSet<>();
        try (Connection connection = DatabaseUtility.getConnection(properties)) {
            String sql = "SELECT make, model FROM car_ads";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String make = resultSet.getString("make");
                        String model = resultSet.getString("model");
                        existingAds.add(make + " " + model);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existingAds;
    }
}
