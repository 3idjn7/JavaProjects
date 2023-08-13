package Utilities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
     private final ExecutorService executor;

     public ThreadManager(int numThreads) {
         executor = Executors.newFixedThreadPool(numThreads);
     }

     public void executeTask(Runnable task) {
         executor.execute(task);
     }

     public void shutdown() {
         executor.shutdown();
         try {
             executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }
}

