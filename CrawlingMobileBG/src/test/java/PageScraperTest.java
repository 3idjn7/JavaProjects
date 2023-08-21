import Utilities.PageScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PageScraperTest {

    @Test
    public void testScrapeAdsFromPage() throws IOException {
        String html = "<table class=\"tablereset\">" +
                "<tr><td class=\"valgtop\"><a class=\"mmm\">Car 1</a></td><td class=\"algright valgtop\"><span class=\"price\">10000</span></td></tr>" +
                "<tr><td class=\"valgtop\"><a class=\"mmm\">Car 2</a></td><td class=\"algright valgtop\"><span class=\"price\">20000</span></td></tr>" +
                "</table>";

        Document document = Jsoup.parse(html);
        assertFalse(PageScraper.scrapeAdsFromPage(document).isEmpty());
    }
}
