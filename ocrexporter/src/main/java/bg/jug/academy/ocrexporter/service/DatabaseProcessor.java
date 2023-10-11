package bg.jug.academy.ocrexporter.service;

import bg.jug.academy.ocrexporter.model.OcrText;
import bg.jug.academy.ocrexporter.repository.OcrTextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseProcessor {

    private final OcrTextRepository ocrTextRepository;

    @Autowired
    public DatabaseProcessor(OcrTextRepository ocrTextRepository) {
        this.ocrTextRepository = ocrTextRepository;
    }

    public void saveTextToDb(String extractedText) {
        OcrText ocrText = new OcrText();
        ocrText.setExtractedText(extractedText);
        ocrTextRepository.save(ocrText);
    }
}
