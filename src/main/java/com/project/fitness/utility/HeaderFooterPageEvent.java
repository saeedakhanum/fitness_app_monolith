package com.project.fitness.utility;

import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

	private Image logo;

	// constructor
	public HeaderFooterPageEvent() {
	    try {
	        var resource = Thread.currentThread()
	                .getContextClassLoader()
	                .getResource("static/logo.png");

	        if (resource != null) {
	            logo = Image.getInstance(resource);
	            logo.scaleAbsolute(40, 40);
	        } else {
	            System.out.println("Logo not found!");
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

	// Header
	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		try {
			PdfPTable header = new PdfPTable(2); // table with 2 column 1 for logo and other for text
			header.setWidthPercentage(100);
			header.setWidths(new int[] { 1, 4 });

			// logo cell
			PdfPCell logoCell = new PdfPCell(logo);
			logoCell.setBorder(Rectangle.NO_BORDER);
			logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);

			// title cell
			Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			PdfPCell textCell = new PdfPCell(new Phrase("Fitness Application Report", font));
			textCell.setBorder(Rectangle.NO_BORDER);
			textCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			header.addCell(logoCell);
			header.addCell(textCell);
			header.setSpacingAfter(10);
			document.add(header);
			document.add(new LineSeparator());
		} catch (DocumentException ex) {
			ex.printStackTrace();
		}
	}
	
	//Footer
	@Override
	public void onEndPage(PdfWriter writer,Document document) {
		PdfContentByte cb = writer.getDirectContent();
		Font font=FontFactory.getFont(FontFactory.HELVETICA,9);
		Phrase footer= new Phrase("Page "+writer.getPageNumber(),font);
		ColumnText.showTextAligned(
				cb,
				Element.ALIGN_CENTER,
				footer,														   //Actual text
				(document.right()-document.left())/2 + document.leftMargin(), //x co ordinate
				document.bottom()-10,										  //y co ordinate
				0															 // rotation
			);
	}

}
