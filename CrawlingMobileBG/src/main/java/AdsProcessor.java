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

import static Utilities.ThreadManager.THREAD_COUNT;

public class AdsProcessor {


    private static int totalAdsProcessed = 0;
    private static int totalAdsAdded = 0;

    public static void main(String[] args) {
        System.out.println("Accessing database.");
        Properties properties = DatabaseUtility.loadDatabasePropertiesFromClasspath("config.properties");
        processAds(properties);
    }

    public static void processAds(Properties properties) {
        boolean isDatabaseNew = isDatabaseEmpty(properties);


        int totalPages = WebUtility.getTotalPages();
        System.out.println("Total pages to process: " + totalPages);

        System.out.println("Activating " + THREAD_COUNT + " threads for maximum warp speed...");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<List<AdListing>>> futures = new ArrayList<>();

        WebUtility.processPages(totalPages, url -> {
            futures.add(executor.submit(() -> {
                try {
                    System.out.println("Processing page: " + url);
                    Document document = Jsoup.connect(url).execute().charset("UTF-8").parse();

                    List<AdListing> adListings = PageScraper.scrapeAdsFromPage(document);
                    try (Connection connection = DatabaseUtility.getConnection(properties)) {
                        for (AdListing adListing : adListings) {
                            String adTitle = adListing.title();
                            String[] titleParts = adTitle.split(" ", 2);
                            String make = titleParts[0];
                            String model = titleParts.length > 1 ? titleParts[1] : "";
                            totalAdsProcessed++;

                            if (!isAdExistsInDatabase(connection, make, model)) {
                                String insertSql = isDatabaseNew ?
                                        "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)" :
                                        "INSERT INTO car_ads (make, model, price, new_flag) VALUES (?, ?, ?, ?)";
                                totalAdsAdded++;
                                System.out.println("Added ad: " + adTitle);

                                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                                    statement.setString(1, make);
                                    statement.setString(2, model);
                                    statement.setString(3, adListing.price());

                                    if (!isDatabaseNew) {
                                        statement.setBoolean(4, true); // Set new_flag to true for new ads
                                    }

                                    statement.executeUpdate();
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    return adListings;
                } catch (IOException e) {
                    System.err.println("Error connecting to URL: " + e.getMessage());
                    return Collections.emptyList();
                } catch (Exception e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }));
        });

        executor.shutdown();

        List<AdListing> adsToAdd = new ArrayList<>();
        for (Future<List<AdListing>> future : futures) {
            try {
                adsToAdd.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (!adsToAdd.isEmpty()) {
            try (Connection connection = DatabaseUtility.getConnection(properties)) {
                String insertSql = isDatabaseNew ?
                        "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)" :
                        "INSERT INTO car_ads (make, model, price, new_flag) VALUES (?, ?, ?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    for (AdListing adListing : adsToAdd) {
                        String adTitle = adListing.title();
                        String[] titleParts = adTitle.split(" ", 2);
                        String make = titleParts[0];
                        String model = titleParts.length > 1 ? titleParts[1] : "";

                        if (!isAdExistsInDatabase(connection, make, model)) {
                            statement.setString(1, make);
                            statement.setString(2, model);
                            statement.setString(3, adListing.price());

                            if (!isDatabaseNew) {
                                statement.setBoolean(4, true); // Set new_flag to true for new ads
                            }

                            statement.executeUpdate();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Ads processing completed.");
        System.out.println("Total ads processed: " + totalAdsProcessed);
        System.out.println("Total ads added: " + totalAdsAdded);

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