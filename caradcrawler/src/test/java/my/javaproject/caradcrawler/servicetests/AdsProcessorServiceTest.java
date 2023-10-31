package my.javaproject.caradcrawler.servicetests;

import jakarta.transaction.Transactional;
import my.javaproject.caradcrawler.model.AdListing;
import my.javaproject.caradcrawler.repository.CarAdRepository;
import my.javaproject.caradcrawler.service.AdsProcessorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AdsProcessorServiceTest {

    @Autowired
    private AdsProcessorService adsProcessorService;

    @Autowired
    private CarAdRepository carAdRepository;

    @BeforeEach
    public void setup() {

    }

    @AfterEach
    public void cleanup() {

    }
    @Transactional
    @Test
    public void testIsDatabaseEmpty() {
        boolean result = adsProcessorService.isDatabaseEmpty();
        assertFalse(result, "Expected the test database not to be empty.");
    }

    @Test
    public void testProcessPage() throws ExecutionException, InterruptedException {
        int testPage = 1;
        boolean checkForDuplicates = false;

        List<AdListing> result = adsProcessorService.processPage(testPage, checkForDuplicates).get();

        assertNotNull(result, "Expected the result to be not null.");
        assertFalse(result.isEmpty(), "Expected to have ads listings.");
    }

    @Test
    public void testNoDuplicateAds() throws ExecutionException, InterruptedException {
        int testPage = 1;
        boolean checkForDuplicates = true;

        List<AdListing> result = adsProcessorService.processPage(testPage, checkForDuplicates).get();

        // Ensure all ads are distinct by their IDs (or another unique field)
        long uniqueAdsCount = result.stream().distinct().count();
        assertEquals(result.size(), uniqueAdsCount, "Expected no duplicate ads listings.");
    }

    @Test
    public void testValidAdListings() throws ExecutionException, InterruptedException {
        int testPage = 1;
        boolean checkForDuplicates = false;

        List<AdListing> result = adsProcessorService.processPage(testPage, checkForDuplicates).get();

        // Ensure each ad listing has valid data, e.g., non-empty title, valid price, etc.
        for (AdListing ad : result) {
            assertNotNull(ad.getTitle(), "Expected title to be not null.");

            // Convert price to int and check
            String cleanedPrice = ad.getPrice().replaceAll("[^0-9]", "");  // Remove non-digit characters

            try {
                int price = Integer.parseInt(cleanedPrice);
                assertTrue(price > 0, "Expected price to be greater than 0.");
            } catch (NumberFormatException e) {
                fail("Price '" + ad.getPrice() + "' is not a valid number.");
            }
        }
    }



    @Test
    public void testAdsCountInDatabase() throws ExecutionException, InterruptedException {
        long initialCount = carAdRepository.count();
        int testPage = 1;
        boolean checkForDuplicates = false;

        adsProcessorService.processPage(testPage, checkForDuplicates).get();

        long afterProcessCount = carAdRepository.count();
        assertTrue(afterProcessCount > initialCount, "Expected the count of ads in database to increase.");
    }
}
