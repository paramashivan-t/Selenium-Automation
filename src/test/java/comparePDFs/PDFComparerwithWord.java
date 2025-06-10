package comparePDFs;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

public class PDFComparerwithWord {
	public static void main(String[] args) {
		String pdf1Path = "C:\\Users\\Paramasivan.T\\eclipse-workspace\\NovoNordisk\\PDF folder\\약리연구2실 General Template (1).pdf";
		String pdf2Path = "C:\\Users\\Paramasivan.T\\eclipse-workspace\\NovoNordisk\\PDF folder\\2  General Template 2025-05-05 (2).pdf";

		String pdf1Name = new File(pdf1Path).getName();
		String pdf2Name = new File(pdf2Path).getName();
		// Dynamically generate output file name
		String outputWordFile = System.getProperty("user.dir") + "/" + pdf1Name + "_vs_" + pdf2Name + ".docx";
		// Print the dynamically generated file path
		System.out.println("✅ Word document created: " + outputWordFile);
		try {
			// Metadata details
			String executionDate = new SimpleDateFormat("ddMMMyyyy HH:mm:ss").format(new Date());
			String user = System.getProperty("user.name");
			String machineName = InetAddress.getLocalHost().getHostName();
			// Extract text page-wise from both PDFs
			Map<String, List<Integer>> pdf1Keywords = extractTextWithPageNumbers(pdf1Path);
			Map<String, List<Integer>> pdf2Keywords = extractTextWithPageNumbers(pdf2Path);
			// Compare text and get results with page numbers
			List<Map<String, String>> comparisonResults = compareTextWithPages(pdf1Keywords, pdf2Keywords);
			// Write results to Word document
			writeComparisonToWord(comparisonResults, outputWordFile, executionDate, user, machineName, pdf1Name,
					pdf2Name, pdf1Keywords, pdf2Keywords);
		} catch (IOException e) {
			System.out.println("Error processing files: " + e.getMessage());
		}
	}

	// Extracts text from a PDF page-by-page and stores keywords with page numbers
	public static Map<String, List<Integer>> extractTextWithPageNumbers(String filePath) throws IOException {
		PDDocument document = PDDocument.load(new File(filePath));
		PDFTextStripper pdfStripper = new PDFTextStripper();
		Map<String, List<Integer>> keywordMap = new HashMap<>();
		int totalPages = document.getNumberOfPages();
		for (int i = 1; i <= totalPages; i++) {
			pdfStripper.setStartPage(i);
			pdfStripper.setEndPage(i);
			String pageText = pdfStripper.getText(document);
			// Split text into words and store with page numbers
			for (String word : pageText.split("\\s+")) {
				keywordMap.computeIfAbsent(word, k -> new ArrayList<>()).add(i);
			}
		}
		document.close();
		return keywordMap;
	}

	// Compares text from both PDFs and finds common and unique keywords & page
	// numbers
	public static List<Map<String, String>> compareTextWithPages(Map<String, List<Integer>> pdf1Data,
			Map<String, List<Integer>> pdf2Data) {
		Set<String> commonWords = new HashSet<>(pdf1Data.keySet());
		commonWords.retainAll(pdf2Data.keySet()); // Find common words
		Set<String> uniquePdf1Words = new HashSet<>(pdf1Data.keySet());
		uniquePdf1Words.removeAll(pdf2Data.keySet()); // Find unique words in PDF1
		Set<String> uniquePdf2Words = new HashSet<>(pdf2Data.keySet());
		uniquePdf2Words.removeAll(pdf1Data.keySet()); // Find unique words in PDF2
		List<Map<String, String>> results = new ArrayList<>();
		for (String word : commonWords) {
			Map<String, String> row = new HashMap<>();
			row.put("PDF1", "Present");
			row.put("PDF2", "Present");
			row.put("Keyword", word);
			row.put("Page", "PDF1: " + pdf1Data.get(word) + " | PDF2: " + pdf2Data.get(word));
			results.add(row);
		}
		for (String word : uniquePdf1Words) {
			Map<String, String> row = new HashMap<>();
			row.put("PDF1", "Present");
			row.put("PDF2", "Absent");
			row.put("Keyword", word);
			row.put("Page", "PDF1: " + pdf1Data.get(word));
			results.add(row);
		}
		for (String word : uniquePdf2Words) {
			Map<String, String> row = new HashMap<>();
			row.put("PDF1", "Absent");
			row.put("PDF2", "Present");
			row.put("Keyword", word);
			row.put("Page", "PDF2: " + pdf2Data.get(word));
			results.add(row);
		}
		return results;
	}

	// Writes comparison results to a Word document with colors and highlights
	public static void writeComparisonToWord(List<Map<String, String>> data, String filePath, String date, String user,
			String machine, String pdf1Name, String pdf2Name, Map<String, List<Integer>> pdf1Keywords,
			Map<String, List<Integer>> pdf2Keywords) throws IOException {
		XWPFDocument document = new XWPFDocument();
		FileOutputStream out = new FileOutputStream(filePath);
		// Adding title
		XWPFParagraph titlePara = document.createParagraph();
		XWPFRun titleRun = titlePara.createRun();
		titleRun.setText("PDF Comparison");
		titleRun.setBold(true);
		titleRun.setFontSize(20);
		titleRun.addBreak();
		// Adding metadata
		XWPFParagraph metaPara = document.createParagraph();
		XWPFRun metaRun = metaPara.createRun();
		metaRun.setText("Execution Date: " + date);
		metaRun.addBreak();
		metaRun.setText("User: " + user);
		metaRun.addBreak();
		metaRun.setText("Machine Name: " + machine);
		metaRun.setBold(true);
		metaRun.addBreak();
		// Creating table with dynamic PDF names
		XWPFTable table = document.createTable();
		XWPFTableRow headerRow = table.getRow(0);
		headerRow.getCell(0).setText("Keyword");
		headerRow.addNewTableCell().setText("PDF1:" + pdf1Name);
		headerRow.addNewTableCell().setText("PDF2:" + pdf2Name);
		headerRow.addNewTableCell().setText("Page Number");

		// Center-align header row
		for (int i = 0; i < headerRow.getTableCells().size(); i++) {
			headerRow.getCell(i).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
		}

		// Filling data into the table with colors and highlights
		for (Map<String, String> row : data) {
			XWPFTableRow dataRow = table.createRow();
			XWPFRun run1 = dataRow.getCell(0).getParagraphs().get(0).createRun();
			XWPFRun run2 = dataRow.getCell(1).getParagraphs().get(0).createRun();
			XWPFRun run3 = dataRow.getCell(2).getParagraphs().get(0).createRun();
			XWPFRun run4 = dataRow.getCell(3).getParagraphs().get(0).createRun();
			run1.setText(row.get("Keyword"));
			run2.setText(row.get("PDF1"));
			run3.setText(row.get("PDF2"));
			run4.setText(row.get("Page"));

			// Center-align data rows
			dataRow.getCell(0).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
			dataRow.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
			dataRow.getCell(2).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
			dataRow.getCell(3).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

			if (row.get("PDF1").equals("Absent")) {
				run2.setColor("FF0000"); // Red for absent in PDF1
			} else if (row.get("PDF2").equals("Absent")) {
				run3.setColor("FF0000"); // Red for absent in PDF2
			}
//            if (row.get("PDF1").equals("Present") && row.get("PDF2").equals("Present")) {
//                run3.setColor("FFFF00");
//                run3.setBold(true);
//            }
		}
		// Save Word file
		document.write(out);
		out.close();
		document.close();
	}
}
