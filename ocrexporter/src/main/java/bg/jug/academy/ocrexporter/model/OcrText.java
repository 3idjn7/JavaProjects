package bg.jug.academy.ocrexporter.model;

import jakarta.persistence.*;

@Entity
public class OcrText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }
}
