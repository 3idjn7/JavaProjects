package bg.jug.academy.ocrexporter.servicetest;

import bg.jug.academy.ocrexporter.model.OcrApiResponse;
import bg.jug.academy.ocrexporter.service.OcrApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OcrApiServiceTest {

    @InjectMocks
    private OcrApiService ocrApiService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void extractTextFromImageSuccessTest() {
        // Given
        String imageUrl = "https://i.stack.imgur.com/t3qWG.png";
        String apiResponseText = "Example text";
        OcrApiResponse apiResponse = mockApiSuccessResponse(apiResponseText);

        when(restTemplate.postForEntity(anyString(), any(), eq(OcrApiResponse.class)))
                .thenReturn(ResponseEntity.ok(apiResponse));

        // When
        String extractedText = ocrApiService.extractTextFromImage(imageUrl);

        // Then
        assertThat(extractedText).isEqualTo(apiResponseText);
    }

    @Test
    void extractTextFromImageFailureTest() {
        // Given
        String imageUrl = "http://example.com/image.jpg";

        // Mocking RestTemplate to return null
        when(restTemplate.postForEntity(anyString(), any(), eq(OcrApiResponse.class)))
                .thenReturn(null);

        // When & Then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> ocrApiService.extractTextFromImage(imageUrl))
                .withMessageContaining("Received null response from OCR API");
    }

    private OcrApiResponse mockApiSuccessResponse(String text) {
        OcrApiResponse apiResponse = new OcrApiResponse();
        OcrApiResponse.ParsedResult parsedResult = new OcrApiResponse.ParsedResult();
        parsedResult.setParsedText(text);
        apiResponse.setParsedResults(List.of(parsedResult));
        return apiResponse;
    }
}
