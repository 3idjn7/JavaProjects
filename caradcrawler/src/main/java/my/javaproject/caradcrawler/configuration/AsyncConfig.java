package my.javaproject.caradcrawler.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Value("${thread.count}")
    private int THREAD_COUNT;

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        return Executors.newFixedThreadPool(THREAD_COUNT);
    }
}