package my.javaproject.caradcrawler.servicetests;

import my.javaproject.caradcrawler.components.PageScraper;
import my.javaproject.caradcrawler.components.WebUtility;
import my.javaproject.caradcrawler.model.AdListing;
import my.javaproject.caradcrawler.repository.CarAdRepository;
import my.javaproject.caradcrawler.service.AdsProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AdsProcessorMocking {

    @Mock
    private CarAdRepository carAdRepository;

    @Mock
    private PageScraper pageScraper;

    @Mock
    private WebUtility webUtility;

    @InjectMocks
    private AdsProcessorService adsProcessorService;

    @Test
    public void testProcessPageWithoutDuplicateCheck() throws ExecutionException, InterruptedException, IOException {
        // Arrange
        int page = 1;
        boolean checkForDuplicates = false;

        AdListing adListing = new AdListing("TestCar 1", "$1000");

        when(webUtility.buildUrlForPage(anyInt())).thenReturn("http://example.com");
        when(pageScraper.scrapeAdsFromPage(any())).thenReturn(List.of(adListing));

        // Act
        CompletableFuture<List<AdListing>> resultFuture = adsProcessorService.processPage(page, checkForDuplicates);
        List<AdListing> result = resultFuture.get();

        // Assert
        verify(webUtility).buildUrlForPage(page);
        verify(pageScraper).scrapeAdsFromPage(any());
        assertFalse(result.isEmpty());
        assertEquals("TestCar 1", result.get(0).getTitle());
    }

}

