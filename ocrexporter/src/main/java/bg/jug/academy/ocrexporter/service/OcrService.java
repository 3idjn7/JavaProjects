package bg.jug.academy.ocrexporter.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OcrService {

    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);

    @Autowired
    private OcrApiService ocrApiService;

    @Autowired
    private TextFileProcessor textFileProcessor;

    @Autowired
    private PdfProcessor pdfProcessor;

    @Autowired
    private DatabaseProcessor databaseProcessor;

    public void processFile(String url, String format, String location) {
        String extractedText = ocrApiService.extractTextFromImage(url);

        switch (format.toLowerCase()) {
            case "pdf" -> pdfProcessor.createPdf(extractedText, location);
            case "text" -> textFileProcessor.saveAsTextFile(extractedText, location);
            case "db" -> databaseProcessor.saveTextToDb(extractedText);
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        }
    }

}
