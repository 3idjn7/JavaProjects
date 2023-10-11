package bg.jug.academy.ocrexporter.repositorytest;

import bg.jug.academy.ocrexporter.model.OcrText;
import bg.jug.academy.ocrexporter.repository.OcrTextRepository;
import bg.jug.academy.ocrexporter.service.DatabaseProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DatabaseProcessorTest {

    @InjectMocks
    private DatabaseProcessor databaseProcessor;

    @Mock
    private OcrTextRepository ocrTextRepository;

    @Test
    void saveTextToDbTest() {
        // Given
        String extractedText = "Extracted Text Example";

        // When
        databaseProcessor.saveTextToDb(extractedText);

        // Then
        verify(ocrTextRepository, times(1)).save(any(OcrText.class));
    }
}
