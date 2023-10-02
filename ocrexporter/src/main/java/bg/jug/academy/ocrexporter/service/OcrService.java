package bg.jug.academy.ocrexporter.service;



import bg.jug.academy.ocrexporter.model.OcrApiResponse;
import bg.jug.academy.ocrexporter.model.OcrText;
import bg.jug.academy.ocrexporter.repository.OcrTextRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class OcrService {

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("apikey", apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("url", imageUrl);
        body.add("language", "eng");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<OcrApiResponse> response = restTemplate.postForEntity(apiUrl, request, OcrApiResponse.class);
        if(response.getBody() != null && response.getBody().getParsedResults() != null && !response.getBody().getParsedResults().isEmpty()) {
            return response.getBody().getParsedResults().get(0).getParsedText();
        } else {
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
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(text);
                contentStream.endText();
            }
            doc.save(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error creating PDF", e);
        }
    }

    private void saveAsTextFile(String text, String filePath) {
        try {
            Files.write(Paths.get(filePath), text.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to text file", e);
        }
    }
}
