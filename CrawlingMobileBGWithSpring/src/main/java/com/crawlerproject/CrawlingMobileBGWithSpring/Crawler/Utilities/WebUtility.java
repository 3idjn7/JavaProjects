package com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.function.Consumer;

public class WebUtility {

    public static final String BASE_URL = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=tqzxar";
    private final RestTemplate restTemplate;

    public WebUtility(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Document fetchHtmlContent(String url) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String htmlContent = response.getBody();
        return Jsoup.parse(htmlContent);
    }

    public static void processPages(int totalPages, Consumer<String> pageProcessor) {
        for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
            String url = BASE_URL + "&f1=" + currentPage;
            pageProcessor.accept(url);
        }
    }

    public static int getTotalPages() {
        try {
            Document document = Jsoup.connect(BASE_URL).get();
            String totalPagesText = document.selectFirst("span.pageNumbersInfo b").text();
            String[] parts = totalPagesText.split(" ");
            if (parts.length >= 4) {
                return Integer.parseInt(parts[3]);
            } else {
                System.err.println("Total pages info format unexpected.");
            }
        } catch (Exception e) {
            System.err.println("Error connecting to URL: " + e.getMessage());
        }
        return 0;
    }
}
