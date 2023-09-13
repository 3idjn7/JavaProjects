import Utilities.AdListing;
import Utilities.DatabaseUtility;
import Utilities.PageScraper;
import Utilities.WebUtility;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static Utilities.ThreadManager.THREAD_COUNT;

public class AdsProcessor {

    private static final AtomicInteger totalAdsProcessed = new AtomicInteger(0);
    private static final AtomicInteger totalAdsAdded = new AtomicInteger(0);
    private static final AtomicInteger totalAdsUpdated = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("Accessing database.");
        Properties properties = DatabaseUtility.loadDatabasePropertiesFromClasspath("config.properties");
        boolean isDatabaseNew = isDatabaseEmpty(properties);

        if (isDatabaseNew) {
            processAdsWhenDatabaseIsEmpty(properties);
        } else {
            processAdsWhenDatabaseIsNotEmpty(properties);
        }
    }

    public static void processAdsWhenDatabaseIsEmpty(Properties properties) {
        int totalPages = WebUtility.getTotalPages();

        System.out.println("Activating " + THREAD_COUNT + " threads for maximum warp speed...");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<List<AdListing>>> futures = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            final int page = i;
            futures.add(executor.submit(() -> {
                int addedAdsCount = 0;
                try {
                    String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=tqzxar&f1=" + page;
                    System.out.println("Processing page: " + url);
                    Document document = Jsoup.connect(url).execute().charset("UTF-8").parse();

                    List<AdListing> adListings = PageScraper.scrapeAdsFromPage(document);
                    try (Connection connection = DatabaseUtility.getConnection(properties)) {
                        for (AdListing adListing : adListings) {
                            String adTitle = adListing.title();
                            String[] titleParts = adTitle.split(" ", 2);
                            String make = titleParts[0];
                            String model = titleParts.length > 1 ? titleParts[1] : "";

                            String insertSql = "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)";
                            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                                statement.setString(1, make);
                                statement.setString(2, model);
                                statement.setString(3, adListing.price());
                                int rowsAffected = statement.executeUpdate();
                                if (rowsAffected > 0) {
                                    addedAdsCount++;
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    totalAdsProcessed.addAndGet(adListings.size());
                    totalAdsAdded.addAndGet(addedAdsCount);

                    return adListings;
                } catch (IOException e) {
                    System.err.println("Error connecting to URL: " + e.getMessage());
                    return Collections.emptyList();
                } catch (Exception e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Ads processing completed.");
        System.out.println("Total pages processed: " + totalPages);
        System.out.println("Total ads processed: " + totalAdsProcessed);
        System.out.println("Total ads added: " + totalAdsAdded);

    }

    public static void processAdsWhenDatabaseIsNotEmpty(Properties properties) {
        int totalPages = WebUtility.getTotalPages();

        System.out.println("Activating " + THREAD_COUNT + " threads for maximum warp speed...");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<List<AdListing>>> futures = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            final int page = i;
            futures.add(executor.submit(() -> {
                int updatedAdsCount = 0;
                try {
                    String url = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=tqzxar&f1=" + page;
                    System.out.println("Processing page: " + url);
                    Document document = Jsoup.connect(url).execute().charset("UTF-8").parse();

                    List<AdListing> adListings = PageScraper.scrapeAdsFromPage(document);
                    try (Connection connection = DatabaseUtility.getConnection(properties)) {
                        for (AdListing adListing : adListings) {
                            String adTitle = adListing.title();
                            String[] titleParts = adTitle.split(" ", 2);
                            String make = titleParts[0];
                            String model = titleParts.length > 1 ? titleParts[1] : "";

                            if (ifAdsExistsInDatabase(connection, make, model)) {
                                String insertSql = "INSERT INTO car_ads (make, model, price, new_flag) VALUES (?, ?, ?, ?)";
                                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                                    statement.setString(1, make);
                                    statement.setString(2, model);
                                    statement.setString(3, adListing.price());
                                    statement.setBoolean(4, true); // Set new_flag to true for new ads
                                    int rowsAffected = statement.executeUpdate();
                                    if (rowsAffected > 0) {
                                        updatedAdsCount++;
                                    }
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    totalAdsProcessed.addAndGet(adListings.size());
                    totalAdsUpdated.addAndGet(updatedAdsCount);

                    return adListings;
                } catch (IOException e) {
                    System.err.println("Error connecting to URL: " + e.getMessage());
                    return Collections.emptyList();
                } catch (Exception e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Ads processing completed.");
        System.out.println("Total pages processed: " + totalPages);
        System.out.println("Total ads processed: " + totalAdsProcessed);
        System.out.println("Total ads updated: " + totalAdsUpdated);
    }

    private static boolean isDatabaseEmpty(Properties properties) {
        try (Connection connection = DatabaseUtility.getConnection(properties)) {
            String sql = "SELECT COUNT(*) FROM car_ads";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean ifAdsExistsInDatabase(Connection connection, String make, String model) {
        String sql = "SELECT COUNT(*) FROM car_ads WHERE make = ? AND model = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, make);
            statement.setString(2, model);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count <= 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}