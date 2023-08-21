import Utilities.ThreadManager;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadManagerTest {

    @Test
    public void testThreadExecution() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        Runnable task = () -> {
            // Simulate some work
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        };

        ThreadManager threadManager = ThreadManager.getInstance();
        threadManager.executeTask(task);
        threadManager.executeTask(task);
        threadManager.executeTask(task);

        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }
}
