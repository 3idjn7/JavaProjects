package bg.jug.academy.ocrexporter.service;



import bg.jug.academy.ocrexporter.model.OcrApiResponse;
import bg.jug.academy.ocrexporter.model.OcrText;
import bg.jug.academy.ocrexporter.repository.OcrTextRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class OcrService {

    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);

    @Value("${ocr.api.key}")
    private String apiKey;

    @Autowired
    private OcrTextRepository ocrTextRepository;


    public void processFile(String url, String format, String location) {
        String extractedText = extractTextFromImage(url);

        switch (format.toLowerCase()) {
            case "pdf":
                createPdf(extractedText, location);
                break;
            case "text" :
                saveAsTextFile(extractedText, location);
                break;
            case "db" :
                saveTextToDb(extractedText);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported format: " + format);
        }
    }

    private String extractTextFromImage(String imageUrl) {
        String apiUrl = "https://api.ocr.space/Parse/Image";

        logger.info("Sending request to OCR API...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("apikey", apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("url", imageUrl);
        body.add("language", "eng");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<OcrApiResponse> response = restTemplate.postForEntity(apiUrl, request, OcrApiResponse.class);

        logger.info("Received response from OCR API");

        if(response.getBody() != null && response.getBody().getParsedResults() != null && !response.getBody().getParsedResults().isEmpty()) {
            logger.info("Text extracted successfuly");
            return response.getBody().getParsedResults().get(0).getParsedText();
        } else {
            logger.error("No text parsed from the image");
            throw new RuntimeException("No text parsed from the image");
        }
    }

    public void saveTextToDb(String extractedText) {
        OcrText ocrText = new OcrText();
        ocrText.setExtractedText(extractedText);
        ocrTextRepository.save(ocrText);
    }

    private void createPdf(String text, String filePath) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();

                float margin = 100;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float yPosition = yStart;
                float leading = 1.5f * 12;
                contentStream.newLineAtOffset(margin, yPosition);

                String[] lines = text.replace("\r", "").split("\n");
                for(String line : lines) {
                    contentStream.showText(line);
                    yPosition -= leading;
                    contentStream.newLineAtOffset(0, -leading);
                }
                contentStream.endText();
            }
            doc.save(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error creating PDF", e);
        }
    }

    private void saveAsTextFile(String text, String filePath) {
        logger.info("Saving extracted text to file...");
        try {
            Files.write(Paths.get(filePath), text.getBytes(), StandardOpenOption.CREATE);
            logger.info("Text saved successfully to: " + filePath);
        } catch (IOException e) {
            logger.error("Error writing to text file", e);
            throw new RuntimeException("Error writing to text file", e);
        }
    }
}
