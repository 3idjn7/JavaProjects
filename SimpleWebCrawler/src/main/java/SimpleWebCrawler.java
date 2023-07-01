import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SimpleWebCrawler {

    public static void main(String[] args) {
        String url = "https://mobile.bg";

        try {
            Document document = Jsoup.connect(url).get();

            String title = document.title();
            System.out.println("Title: " + title);


            String htmlContent = document.html();
            System.out.println("HTML Content:\n" + htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
