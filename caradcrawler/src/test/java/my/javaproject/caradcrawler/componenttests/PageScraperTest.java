package my.javaproject.caradcrawler.componenttests;

import my.javaproject.caradcrawler.components.PageScraper;
import my.javaproject.caradcrawler.model.AdListing;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PageScraperTest {

    @Mock
    private Document document;

    @Mock
    private Element adElement;

    @Mock
    private Element titleElement;

    @Mock
    private Element priceElement;

    @Mock
    private Elements adElements;

    private PageScraper pageScraper;

    @BeforeEach
    public void setUp() {
        pageScraper = new PageScraper();
    }

    @Test
    public void testScrapeAdsFromPage() {
        // Setup mocked Jsoup Document
        when(document.select("table.tablereset")).thenReturn(adElements);
        when(adElements.iterator()).thenReturn(List.of(adElement).iterator());

        when(adElement.selectFirst("td.valgtop a.mmm")).thenReturn(titleElement);
        when(titleElement.text()).thenReturn("ExampleAdTitle");

        when(adElement.selectFirst("td.algright.valgtop span.price")).thenReturn(priceElement);
        when(priceElement.text()).thenReturn("ExampleAdPrice");

        // Execute
        List<AdListing> result = pageScraper.scrapeAdsFromPage(document);

        // Verify
        assertEquals(1, result.size());
        assertEquals("ExampleAdTitle", result.get(0).getTitle());
        assertEquals("ExampleAdPrice", result.get(0).getPrice());
    }
}



