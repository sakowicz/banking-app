import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Szymon Sakowicz
 */
public class GeneratePDF {

	private static String name, fileName;
	// private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	// Font.BOLD);

	public GeneratePDF() {
		Database db = new Database();
		try {
			name = db.nameById(db.getSessionID());
			String date = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
			Document doc = new Document();
			fileName = (name + " " + date + " Raport.pdf");
			PdfWriter.getInstance(doc, new FileOutputStream(fileName));
			doc.open();
			BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.EMBEDDED);

			Font catFont = new Font(bf, 20, Font.BOLD);
			Font normalFont = new Font(bf, 12, Font.NORMAL);
			Font whiteFont = new Font(bf, 12, Font.NORMAL, GrayColor.GRAYWHITE);
			Font subFont = new Font(bf, 18, Font.BOLD);
			int howMuchIncomes = db.howMuchIncomesByCategory();
			String incomes[][] = db.incomesByCategory();
			int howMuchOutcomes = db.howMuchOutcomesByCategory();
			String outcomes[][] = db.outcomesByCategory();

			doc.add(new Paragraph("Raport operacji na koncie", catFont));
			doc.add(new Paragraph(name, subFont));
			doc.add(new Paragraph("Wygenerowano: " + date, normalFont));
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			float[] columnWidths2 = { 15, 5, 5 };
			PdfPTable summaryTable = new PdfPTable(columnWidths2);
			summaryTable.setWidthPercentage(100);
			summaryTable.getDefaultCell().setUseAscender(true);
			summaryTable.getDefaultCell().setUseDescender(true);
			summaryTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			PdfPCell cell3 = new PdfPCell(new Phrase("Podsumowanie obrotów na rachunku", whiteFont));
			cell3.setBackgroundColor(GrayColor.GRAYBLACK);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setColspan(3);
			summaryTable.addCell(cell3);
			summaryTable.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));

			summaryTable.addCell(new Phrase("Rodzaj", normalFont));
			summaryTable.addCell(new Phrase("Iloœæ transakcji", normalFont));
			summaryTable.addCell(new Phrase("Kwota", normalFont));

			summaryTable.setHeaderRows(3);
			summaryTable.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
			summaryTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
			summaryTable.addCell(new Phrase("Uznania", normalFont));

			summaryTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
			summaryTable.addCell(String.valueOf(db.incomes()));
			summaryTable.addCell(String.valueOf(db.howMuchIncomes()));
			summaryTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
			summaryTable.addCell(new Phrase("Obci¹¿enia", normalFont));
			summaryTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
			summaryTable.addCell(String.valueOf(db.outcomes()));
			summaryTable.addCell("-" + String.valueOf(db.howMuchOutcomes()));
			summaryTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
			summaryTable.addCell(new Phrase("£¹cznie", normalFont));
			summaryTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
			summaryTable.addCell(String.valueOf(db.outcomes() + db.incomes()));
			summaryTable.addCell(String.valueOf((db.howMuchIncomes().subtract(db.howMuchOutcomes()))));

			doc.add(summaryTable);
			doc.add(Chunk.NEWLINE);

			float[] columnWidths = { 5, 5 };
			PdfPTable incomesTable = new PdfPTable(columnWidths);
			incomesTable.setWidthPercentage(30);
			incomesTable.getDefaultCell().setUseAscender(true);
			incomesTable.getDefaultCell().setUseDescender(true);
			incomesTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_MIDDLE);
			PdfPCell cell = new PdfPCell(new Phrase("Uznania", whiteFont));
			cell.setBackgroundColor(GrayColor.GRAYBLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(2);
			incomesTable.addCell(cell);
			incomesTable.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
			incomesTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			incomesTable.addCell(new Phrase("Kategoria", normalFont));
			incomesTable.addCell(new Phrase("Kwota", normalFont));
			incomesTable.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
			for (int counter = 0; counter < howMuchIncomes; counter++) {
				incomesTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
				incomesTable.addCell(new Phrase(incomes[counter][0], normalFont));
				incomesTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
				incomesTable.addCell(new Phrase(incomes[counter][1], normalFont));
			}
			doc.add(incomesTable);
			doc.add(Chunk.NEWLINE);

			PdfPTable outcomesTable = new PdfPTable(columnWidths);
			outcomesTable.setWidthPercentage(30);
			outcomesTable.getDefaultCell().setUseAscender(true);
			outcomesTable.getDefaultCell().setUseDescender(true);
			outcomesTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_MIDDLE);
			PdfPCell cell2 = new PdfPCell(new Phrase("Obci¹¿enia", whiteFont));
			cell2.setBackgroundColor(GrayColor.GRAYBLACK);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setColspan(2);
			outcomesTable.addCell(cell2);
			outcomesTable.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
			outcomesTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			outcomesTable.addCell(new Phrase("Kategoria", normalFont));
			outcomesTable.addCell(new Phrase("Kwota", normalFont));
			outcomesTable.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
			for (int counter = 0; counter < howMuchOutcomes; counter++) {
				outcomesTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
				outcomesTable.addCell(new Phrase(outcomes[counter][0], normalFont));
				outcomesTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
				outcomesTable.addCell(new Phrase(outcomes[counter][1], normalFont));
			}
			doc.add(outcomesTable);
			doc.add(Chunk.NEWLINE);

			float[] columnWidth = { 5, 6, 5, 5, 5, 10 };
			PdfPTable table = new PdfPTable(columnWidth);
			table.setWidthPercentage(100);
			table.getDefaultCell().setUseAscender(true);
			table.getDefaultCell().setUseDescender(true);
			table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			PdfPCell cell21 = new PdfPCell(new Phrase("Ostatnie transakcje", whiteFont));
			cell21.setBackgroundColor(GrayColor.GRAYBLACK);
			cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell21.setColspan(6);
			table.addCell(cell21);
			table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
			table.addCell(new Phrase("Data", normalFont));
			table.addCell(new Phrase("Rodzaj", normalFont));
			table.addCell(new Phrase("Od/do kogo", normalFont));
			table.addCell(new Phrase("Kwota", normalFont));
			table.addCell(new Phrase("Kategoria", normalFont));
			table.addCell(new Phrase("Tytu³em", normalFont));
			table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			String baza[][] = db.fillTable();
			for (int counter = 0; counter < db.howMuchTransations(); counter++) {
				table.addCell(new Phrase(baza[counter][0], normalFont));
				table.addCell(new Phrase(baza[counter][1], normalFont));
				table.addCell(new Phrase(baza[counter][2], normalFont));
				table.addCell(new Phrase(baza[counter][3], normalFont));
				table.addCell(new Phrase(baza[counter][4], normalFont));
				table.addCell(new Phrase(baza[counter][5], normalFont));
			}

			doc.add(table);

			////////////////
			doc.close();
			//spolszczenie przycisków yes/no
			UIManager.put("OptionPane.yesButtonText", "Tak");
	        UIManager.put("OptionPane.noButtonText", "Nie");
			int reply = JOptionPane.showConfirmDialog(null, "Pomyslnie stworzono raport. Otworzyc?", "Stworzono raport",
					JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				try {
					Desktop.getDesktop().open(new File(fileName));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "B³¹d w otwieraniu pliku.");
					e.printStackTrace();
				}
			}
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "B³¹d w tworzeniu raportu.");
		}
		db.closeConnection();

	}

}
