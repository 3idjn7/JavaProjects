package bg.jug.academy.ocrexporter.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PdfProcessor.class);

    public void createPdf(String text, String filePath) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                PDType1Font font = PDType1Font.HELVETICA;
                float fontSize = 12;
                contentStream.setFont(font, fontSize);
                contentStream.beginText();

                float margin = 100;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float yPosition = yStart;
                float leading = 1.5f * fontSize;

                contentStream.newLineAtOffset(margin, yPosition);

                String[] lines = text.replace("\r", "").split("\n");
                for(String line : lines) {
                    List<String> linesToDraw = getLinesToDraw(line, tableWidth, font, fontSize);
                    for (String lineToDraw : linesToDraw) {
                        contentStream.showText(lineToDraw);
                        yPosition -= leading;
                        contentStream.newLineAtOffset(0, -leading);
                    }
                }

                contentStream.endText();
            }
            doc.save(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error creating PDF", e);
        }
    }

    private List<String> getLinesToDraw(String line, float maxWidth, PDType1Font font, float fontSize) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = line.split(" ");
        StringBuilder currentLine = new StringBuilder(words[0]);
        for(int i = 1; i < words.length; i++) {
            if (getStringWidth(currentLine + " " + words[i], font, fontSize) < maxWidth) {
                currentLine.append(" ").append(words[i]);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(words[i]);
            }
        }
        lines.add(currentLine.toString());
        return lines;
    }

    private float getStringWidth(String text, PDType1Font font, float fontSize) throws IOException {
        return fontSize * font.getStringWidth(text) / 1000;
    }
}
