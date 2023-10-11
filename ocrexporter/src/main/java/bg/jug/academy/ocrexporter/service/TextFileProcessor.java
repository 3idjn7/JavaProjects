package bg.jug.academy.ocrexporter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class TextFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TextFileProcessor.class);

    public void saveAsTextFile(String text, String filePath) {
        logger.info("Saving extracted text to file...");
        try {
            Files.write(Paths.get(filePath), text.getBytes(), StandardOpenOption.CREATE);
            logger.info("Text saved successfully to: {}", filePath);
        } catch (IOException e) {
            logger.error("Error writing to text file", e);
            throw new RuntimeException("Error writing to text file", e);
        }
    }
}
