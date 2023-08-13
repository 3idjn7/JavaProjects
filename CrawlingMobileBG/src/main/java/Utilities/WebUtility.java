package Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebUtility {

    public static int getTotalPages(String baseUrl) {
        try {
            Document document = Jsoup.connect(baseUrl).get();
            Element totalPagesInfo = document.selectFirst("span.pageNumbersInfo b");

            if (totalPagesInfo != null) {
                String totalPagesText = totalPagesInfo.text();
                String[] parts = totalPagesText.split(" ");
                if (parts.length >= 4) {
                    int totalPages = Integer.parseInt(parts[3]);
                    return totalPages;
                } else {
                    System.err.println("Total pages info format unexpected.");
                }
            } else {
                System.err.println("Total pages info element not found.");
            }
        } catch (IOException e) {
            System.err.println("Error connecting to URL: " + e.getMessage());
        }

        return 0;
    }
}