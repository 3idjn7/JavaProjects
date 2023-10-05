package my.javaproject.caradcrawler.service;

import jakarta.transaction.Transactional;
import my.javaproject.caradcrawler.components.PageScraper;
import my.javaproject.caradcrawler.components.WebUtility;
import my.javaproject.caradcrawler.entity.CarAd;
import my.javaproject.caradcrawler.model.AdListing;
import my.javaproject.caradcrawler.repository.CarAdRepository;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AdsProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(AdsProcessorService.class);

    @Autowired
    private CarAdRepository carAdRepository;
    @Autowired
    private PageScraper pageScraper;
    @Autowired
    private WebUtility webUtility;


    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<List<AdListing>> processPage(int page, boolean checkForDuplicates) {
        // Use CompletableFuture.supplyAsync() to perform the processing in a separate thread.
        // Make sure to handle exceptions appropriately for your use case.
        return CompletableFuture.supplyAsync(() -> {
            List<AdListing> adListings = new ArrayList<>();
            try {
                String url = webUtility.buildUrlForPage(page);
                logger.info("Processing page: {}", url);
                Document document = Jsoup.connect(url).get();

                adListings = pageScraper.scrapeAdsFromPage(document);

                for (AdListing adListing : adListings) {
                    String adTitle = adListing.getTitle();
                    String[] titleParts = adTitle.split(" ", 2);
                    String make = titleParts[0];
                    String model = titleParts.length > 1 ? titleParts[1] : "";

                    // Check if the ad already exists in the database before saving
                    if (checkForDuplicates && existsByMakeAndModelAndPrice(make, model, adListing.getPrice())) {
                        logger.info("Skipping existing ad: Make: {}, Model: {}, Price: {}", make, model, adListing.getPrice());
                        continue; // skip to next iteration
                    }

                    CarAd carAd = new CarAd();
                    carAd.setMake(make);
                    carAd.setModel(model);
                    carAd.setPrice(adListing.getPrice());
                    carAdRepository.save(carAd);
                }
            } catch (IOException e) {
                logger.error("Error connecting to URL", e);
            } catch (Exception e) {
                logger.error("Unexpected error occurred", e);
            }
            return adListings;
        }).exceptionally(ex -> {
            logger.error("Error in processPage: ", ex);
            return Collections.emptyList();  // You may want to return an empty list on error, or handle it differently.
        });
    }






    @Transactional
    public void processAdsWhenDatabaseIsEmpty() {
        int totalPages = webUtility.getTotalPages();
        List<Future<List<AdListing>>> futures = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            futures.add(processPage(i, false));
        }

        futures.forEach(future -> {
            try {
                List<AdListing> adListings = future.get();
                logger.info("Processed {} ads.", adListings.size());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error while processing ads", e);
            }
        });
        logger.info("Ads processing completed.");
    }


    @Transactional
    public void processAdsWhenDatabaseIsNotEmpty() {
        int totalPages = webUtility.getTotalPages();
        List<Future<List<AdListing>>> futures = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            // Passing true as second parameter to check for duplicates
            futures.add(processPage(i, true));
        }

        AtomicInteger totalAdsProcessed = new AtomicInteger();
        futures.forEach(future -> {
            try {
                List<AdListing> adListings = future.get();
                totalAdsProcessed.addAndGet(adListings.size());
                logger.info("Processed {} ads.", adListings.size());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error while processing ads", e);
            }
        });
        logger.info("Ads processing completed. Total ads processed: {}", totalAdsProcessed.get());
    }



    public boolean isDatabaseEmpty() {
        long count = carAdRepository.count();
        logger.info("Total records in the database: {}", count);
        return carAdRepository.count() == 0;
    }


    public boolean existsByMakeAndModelAndPrice(String make, String model, String price) {
        List<CarAd> carAds = carAdRepository.findByMakeAndModelAndPrice(make, model, price);
        boolean exists = carAds != null && !carAds.isEmpty();
        logger.info("Exists by make '{}' and model '{}' and price '{}': {}", make, model, price, exists);
        return exists;
    }
}