package my.javaproject.caradcrawler.components;

import my.javaproject.caradcrawler.model.AdListing;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageScraper {
    public List<AdListing> scrapeAdsFromPage(Document pageDocument) {
        List<AdListing> adListings = new ArrayList<>();

        Elements adElements = pageDocument.select("table.tablereset");
        for (Element adElement : adElements) {
            Element titleElement = adElement.selectFirst("td.valgtop a.mmm");
            Element priceElement = adElement.selectFirst("td.algright.valgtop span.price");

            if (titleElement != null && priceElement != null) {
                String title = titleElement.text();
                String price = priceElement.text();
                adListings.add(new AdListing(title, price));
            }
        }
        return  adListings;
    }
}