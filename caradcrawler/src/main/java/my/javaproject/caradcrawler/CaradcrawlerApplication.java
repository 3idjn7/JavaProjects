package my.javaproject.caradcrawler;

		import my.javaproject.caradcrawler.service.AdsProcessorService;
		import org.springframework.beans.factory.annotation.Autowired;
		import org.springframework.boot.CommandLineRunner;
		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.context.annotation.Bean;
		import org.springframework.scheduling.annotation.EnableAsync;

		import java.util.concurrent.Executors;

@SpringBootApplication
public class CaradcrawlerApplication implements CommandLineRunner {



	@Autowired
	private final AdsProcessorService adsProcessorService;

	public CaradcrawlerApplication(AdsProcessorService adsProcessorService) {
		this.adsProcessorService = adsProcessorService;
	}

	public static void main(String[] args) {
		SpringApplication.run(CaradcrawlerApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (adsProcessorService.isDatabaseEmpty()) {
			adsProcessorService.processAdsWhenDatabaseIsEmpty();
		} else {
			adsProcessorService.processAdsWhenDatabaseIsNotEmpty();
		}
	}
}