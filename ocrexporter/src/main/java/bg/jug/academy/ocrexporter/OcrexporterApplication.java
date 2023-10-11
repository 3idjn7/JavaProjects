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

	public OcrexporterApplication() {
	}

	@Autowired
	public void setOcrService(OcrService ocrService) {
		this.ocrService = ocrService;
	}

	public static void main(String[] args) {
		SpringApplication.run(OcrexporterApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		validateArguments(args);

		String url = args.getOptionValues("url").get(0);
		String format = args.getOptionValues("format").get(0);
		String location = args.getOptionValues("location").get(0);

		ocrService.processFile(url, format, location);
	}

	private void validateArguments(ApplicationArguments args) {
		if(!args.containsOption("url") || !args.containsOption("format") || !args.containsOption("location")) {
			throw new IllegalArgumentException("Required arguments: --url, --format, --location");
		}

		List<String> urlValues = args.getOptionValues("url");
		List<String> formatValues = args.getOptionValues("format");
		List<String> locationValues = args.getOptionValues("location");

		if (urlValues.isEmpty() || formatValues.isEmpty() || locationValues.isEmpty()) {
			throw new IllegalArgumentException("Values must be provided for --url, --format, --location");
		}
	}
}
