package comparePDFs;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PDFComparer {

    public static void main(String[] args) {
        String originalfilepath = "C:\\Users\\Paramasivan.T\\eclipse-workspace\\NovoNordisk\\PDF folder\\약리연구2실 General Template (1).pdf";
        String copyfilepath = "C:\\Users\\Paramasivan.T\\eclipse-workspace\\NovoNordisk\\PDF folder\\2  General Template 2025-05-05 (2).pdf";
        
        try {
            // Extract text from both PDFs
            String text1 = extractTextFromPDF(originalfilepath);
            String text2 = extractTextFromPDF(copyfilepath);

            // Compare text and find similarities & differences
            compareText(text1, text2);

        } catch (IOException e) {
            System.out.println("Error reading PDF files: " + e.getMessage());
        }
    }

    // Method to extract text from a PDF
    public static String extractTextFromPDF(String filePath) throws IOException {
        PDDocument document = PDDocument.load(new File(filePath));
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String extractedText = pdfStripper.getText(document);
        document.close();
        return extractedText;
    }

    // Method to compare text from both PDFs
    public static void compareText(String text1, String text2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(text1.split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(text2.split("\\s+")));

        Set<String> commonWords = new HashSet<>(words1);
        commonWords.retainAll(words2); // Find common words

        Set<String> differences1 = new HashSet<>(words1);
        differences1.removeAll(words2); // Words unique to PDF 1

        Set<String> differences2 = new HashSet<>(words2);
        differences2.removeAll(words1); // Words unique to PDF 2

        System.out.println("🔍 **PDF Comparison Results**");
        System.out.println("✅ Common Words: " + commonWords);
        System.out.println("❌ Unique to original PDF: " + differences1);
        System.out.println("❌ Unique to copied PDF: " + differences2);
    }
}
