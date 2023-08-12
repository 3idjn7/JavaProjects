package Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebUtility {

    public static int getTotalPages(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Element totalPagesInfo = document.selectFirst("span.pageNumbersInfo b");

            if (totalPagesInfo != null) {
                String totalPagesText = totalPagesInfo.text()
                        .replaceAll("[^0-9]+", "");

                try {
                    return Integer.parseInt(totalPagesText);
                } catch (NumberFormatException e) {
                    System.err.println("Error converting total pages text to integer: " + totalPagesText);
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
