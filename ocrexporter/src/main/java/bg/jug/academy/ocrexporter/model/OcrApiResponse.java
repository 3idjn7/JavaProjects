package bg.jug.academy.ocrexporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OcrApiResponse {

    @JsonProperty("ParsedResults")
    private List<ParsedResult> parsedResults;

    public List<ParsedResult> getParsedResults() {
        return parsedResults;
    }

    public void setParsedResults(List<ParsedResult> parsedResults) {
        this.parsedResults = parsedResults;
    }


    public static class ParsedResult {

        @JsonProperty("ParsedText")
        private String parsedText;

        public String getParsedText() {
            return parsedText;
        }

        public void setParsedText(String parsedText) {
            this.parsedText = parsedText;
        }


    }
}
