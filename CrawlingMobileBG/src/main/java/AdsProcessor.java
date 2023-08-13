import Utilities.DatabaseUtility;
import Utilities.WebUtility;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class AdsProcessor {

    private static final int THREAD_COUNT = 5;

    public static void main(String[] args) {
        Properties properties = DatabaseUtility.loadDatabaseProperties("C:\\Users\\sepre\\OneDrive\\Desktop\\JavaProjects\\CrawlingMobileBG\\src\\main\\resources\\config.properties");
        processAds(properties);
    }

    public static void processAds(Properties properties) {
        boolean isDatabaseNew = isDatabaseEmpty(properties);

        String baseUrl = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=td61bn&f1=1";
        int totalPages = WebUtility.getTotalPages(baseUrl);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<List<AdListing>>> futures = new ArrayList<>();

        Set<String> processedAds = new HashSet<>(); // To track processed ads

        for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
            String url = baseUrl.replace("&f1=1", "&f1=" + currentPage);

            futures.add(executor.submit(() -> {
                try {
                    System.out.println("Scraping page: " + url);
                    Document document = Jsoup.connect(url).execute().charset("UTF-8").parse();

                    List<AdListing> adListings = PageScraper.scrapeAdsFromPage(document);
                    try (Connection connection = DatabaseUtility.getConnection(properties)) { // Create a new connection within the lambda
                        for (AdListing adListing : adListings) {
                            String adTitle = adListing.getTitle();
                            String[] titleParts = adTitle.split(" ", 2);
                            String make = titleParts[0];
                            String model = titleParts.length > 1 ? titleParts[1] : "";

                            String adKey = make + " " + model; // Create a unique key for the ad

                            synchronized (processedAds) {
                                if (!processedAds.contains(adKey)) {
                                    processedAds.add(adKey); // Add the ad to the processed set

                                    if (isAdExistsInDatabase(connection, make, model)) {
                                        System.out.println("Duplicate ad found in the database: " + adTitle);
                                    } else {
                                        // Rest of your existing code for adding the ad to the database
                                        String insertSql = isDatabaseNew ?
                                                "INSERT INTO car_ads (make, model, price) VALUES (?, ?, ?)" :
                                                "INSERT INTO car_ads (make, model, price, new_flag) VALUES (?, ?, ?, ?)";

                                        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                                            statement.setString(1, make);
                                            statement.setString(2, model);
                                            statement.setString(3, adListing.getPrice());

                                            if (!isDatabaseNew) {
                                                statement.setBoolean(4, true); // Set new_flag to true for new ads
                                            }

                                            statement.executeUpdate();
                                        }
                                    }
                                } else {
                                    System.out.println("Duplicate ad skipped: " + adTitle);
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
        }

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
                        String adTitle = adListing.getTitle();
                        String[] titleParts = adTitle.split(" ", 2);
                        String make = titleParts[0];
                        String model = titleParts.length > 1 ? titleParts[1] : "";

                        if (!isAdExistsInDatabase(connection, make, model)) {
                            statement.setString(1, make);
                            statement.setString(2, model);
                            statement.setString(3, adListing.getPrice());

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
