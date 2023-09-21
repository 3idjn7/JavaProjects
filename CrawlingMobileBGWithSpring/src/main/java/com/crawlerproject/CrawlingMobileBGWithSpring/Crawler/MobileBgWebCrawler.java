package com.crawlerproject.CrawlingMobileBGWithSpring.Crawler;

import Utilities.*;
import com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.DatabaseUtility;
import com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.ThreadManager;
import com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.WebUtility;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
@SpringBootApplication
public class MobileBgWebCrawler {

    private static final AtomicInteger totalAdsProcessed = new AtomicInteger(0);

    public static void main(String[] args) {
        Properties properties = DatabaseUtility.loadDatabasePropertiesFromClasspath("config.properties");
        System.out.println("Starting data scraping and storing process...");
        int totalPages = WebUtility.getTotalPages();
        System.out.println("Total pages to process: " + totalPages);


        WebUtility.processPages(totalPages, url -> ThreadManager.getInstance().executeTask(() -> {
            System.out.println("Processing page: " + url);
            processPage(url, properties);
        }));

        ThreadManager.shutdown();

        System.out.println("Data scraping and storing completed successfully.");
        System.out.println("Total ads processed: " + totalAdsProcessed);
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
                    totalAdsProcessed.incrementAndGet();

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
