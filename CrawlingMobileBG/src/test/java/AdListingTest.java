import Utilities.AdListing;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdListingTest {

    @Test
    public void testAdListingCreation() {
        AdListing adListing = new AdListing("Car Title", "10000");
        assertEquals("Car Title", adListing.title());
        assertEquals("10000", adListing.price());
    }
}
