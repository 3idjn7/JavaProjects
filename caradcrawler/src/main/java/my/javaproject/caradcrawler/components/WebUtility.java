package my.javaproject.caradcrawler.components;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WebUtility {
    private static final String BASE_URL = "https://www.mobile.bg/pcgi/mobile.cgi?act=3&slink=tztn2s";

    public String buildUrlForPage(int page) {
        return BASE_URL + "&f1=" + page;
    }


    public int getTotalPages() {
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
