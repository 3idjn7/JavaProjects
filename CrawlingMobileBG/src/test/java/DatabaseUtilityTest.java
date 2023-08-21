import Utilities.DatabaseUtility;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseUtilityTest {

    @Test
    public void testGetConnection() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("dbUrl", "jdbc:mysql://localhost:3306/car_ads_db");
        properties.setProperty("dbUser", "root");
        properties.setProperty("dbPassword", "databasepassword");

        Connection connection = DatabaseUtility.getConnection(properties);
        assertNotNull(connection);
    }
}
