package bg.jug.academy.ocrexporter.service;

import bg.jug.academy.ocrexporter.model.OcrApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OcrApiService {

    private static final Logger logger = LoggerFactory.getLogger(OcrApiService.class);

    @Value("${ocr.api.url}")
    private String apiUrl;

    @Value("${ocr.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;


    public String extractTextFromImage(String imageUrl) {

        logger.info("Sending request to OCR API...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("apikey", apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("url", imageUrl);
        body.add("language", "eng");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<OcrApiResponse> response = restTemplate.postForEntity(apiUrl, request, OcrApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Received response from OCR API");

                if (response.getBody() != null
                        && response.getBody().getParsedResults() != null
                        && !response.getBody().getParsedResults().isEmpty()) {

                    String parsedText = response.getBody().getParsedResults().get(0).getParsedText();

                    if (parsedText == null || parsedText.trim().isEmpty()) {
                        logger.error("No text found on the image");
                        throw new RuntimeException("No text found on the image");
                    }

                    logger.info("Text extracted successfully");
                    return parsedText;

                } else {
                    logger.error("No text parsed from the image");
                    throw new RuntimeException("No text parsed from the image");
                }
            } else {
                logger.error("Failed API request. Status: {}", response.getStatusCode());
                throw new RuntimeException("OCR API request failed with status: " + response.getStatusCode());
            }
        } catch(RestClientException e){
            logger.error("API request failed", e);
            throw new RuntimeException("Failed to connect ot the OCR API", e);
        }
    }
}
