package bg.jug.academy.ocrexporter;

import bg.jug.academy.ocrexporter.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class OcrexporterApplication implements ApplicationRunner {

	@Autowired
	private OcrService ocrService;

	public static void main(String[] args) {
		SpringApplication.run(OcrexporterApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if(!args.containsOption("url") || !args.containsOption("format") || !args.containsOption("location")) {
			throw new IllegalArgumentException("Required arguments: --url, --format, --location");
		}

		String url = args.getOptionValues("url").get(0);
		String format = args.getOptionValues("format").get(0);
		String location = args.getOptionValues("location").get(0);

		if (!List.of("pdf", "text", "word", "db").contains(format.toLowerCase())) {
			throw new IllegalArgumentException("Unsupported format: " + format);
		}


		ocrService.processFile(url, format, location);
	}
}
