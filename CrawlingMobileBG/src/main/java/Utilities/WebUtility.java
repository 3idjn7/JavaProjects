package Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.function.Consumer;

public class WebUtility {

    public static final String BASE_URL = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=thlygb";

    public static void processPages(int totalPages, Consumer<String> pageProcessor) {
        for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
            String url = BASE_URL + "&f1=" + currentPage;
            pageProcessor.accept(url);
        }
    }

    public static int getTotalPages() {
        try {
            Document document = Jsoup.connect(BASE_URL).get();
            Element totalPagesInfo = document.selectFirst("span.pageNumbersInfo b");

            if (totalPagesInfo != null) {
                String totalPagesText = totalPagesInfo.text();
                String[] parts = totalPagesText.split(" ");
                if (parts.length >= 4) {
                    return Integer.parseInt(parts[3]);
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
