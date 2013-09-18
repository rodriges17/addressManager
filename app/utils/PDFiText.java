package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import models.Contact;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

public class PDFiText {
	
	public static void generateLabels(List<Contact> contacts) {
		System.out.println("Chapter 10 example 7: Columns");

		// step 1: creation of a document-object
		Document document = new Document();

		try {

			// step 2:
			// we create a writer that listens to the document
			// and directs a PDF-stream to a file
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("output/labels.pdf"));

			
			// step 3: we open the document
			document.open();

			// step 4:
//            BaseFont bf = BaseFont.createFont("Helvetica", "Cp1252", false);
//            Font font = new Font(bf, 11, Font.NORMAL);
//                        
//            // we grab the ContentByte and do some stuff with it
//            //PdfContentByte cb = writer.getDirectContent();
//            PdfContentByte cb = writer.getDirectContent();
//            ColumnText ct = new ColumnText(cb);   
//            float[] left = {20, 210, 400};
//            float[] right = {190, 380, 570};
//            
//            for(int i = 0; i <= 49; i++) {
//            for(Contact contact : contacts) {
//            	ct.addText(new Phrase((contact.title == null  ? " " : contact.title) + "\n" + contact.firstName + " " + contact.name + "\n" + contact.street
//            							+ "\n" + contact.city + "\n" + (contact.country == null  ? " " : contact.country) + "\n"));
//            }
//            }
//            
//            ct.addText(new Phrase("First Middle Last How does a long one affect the code?\nGroup\nStreet\nCity/AB  Postal\n", new Font(Font.HELVETICA, 12)));            
//            ct.addText(new Phrase("First Middle Last\nGroup\nStreet\nCity/AB  Postal\n", new Font(Font.HELVETICA, 12)));            
//            ct.addText(new Phrase("First Middle Last How does a long one affect the code?\nGroup\nStreet\nCity/AB  Postal\n", new Font(Font.HELVETICA, 12)));            
//            int status = 0;
//            int column = 0;
//            while((status & ColumnText.NO_MORE_TEXT) == 0) {
//                System.out.println("page " + writer.getPageNumber() + " column " + column);       
//                ct.setSimpleColumn(left[column], 20, right[column], 810, 16, Element.ALIGN_JUSTIFIED); //llx lly urx ury leading align
//                status = ct.go();
//                if ((status & ColumnText.NO_MORE_COLUMN) != 0) {
//                    column++;
//                    if (column > 2) {
//                        document.newPage();
//                        column = 0;
//                    }
//                }
//            }
            
			// step 4:
            BaseFont bf = BaseFont.createFont("Helvetica", "Cp1252", false);
            Font font = new Font(bf, 11, Font.NORMAL);
                        
            // we grab the ContentByte and do some stuff with it
            PdfContentByte cb = writer.getDirectContent();
            ColumnText ct = new ColumnText(cb);
            String[] address = new String[contacts.size()];
            float[] left = {20, 210, 400};
            float[] right = {190, 380, 570};
            float[] verticalPosition = {720, 620, 520, 420, 320, 220, 120, 20};

            int i=0;
			for(Contact contact : contacts) {
            address[i] = (contact.title == null  ? " " : contact.title) + "\n" + contact.firstName + " " + contact.name + "\n" + contact.street
					+ "\n" + contact.city + "\n" + (contact.country == null  ? " " : contact.country) + "\n";
            System.out.println(address[i]);
            i++;
			}            

            int horizontal = 3;
            int vertical = 8;

            int label = 1;
            int column = 1;

            for(int j=0; j<contacts.size(); j++) {
                System.out.println("page " + writer.getPageNumber() + " vertical " + label + " horizontal " + column);       
				ct.setSimpleColumn((new Phrase(address[j], new Font(Font.HELVETICA, 12))), left[column-1],
					verticalPosition[label-1], right[column-1], verticalPosition[label-1] + 90, 16,
					Element.ALIGN_JUSTIFIED);
				int test = ct.go();
				//ct.clearChunks();

                column++;
				if (column >3) {
                    label++;
					column = 1;
                    if (label > 8) {
                        document.newPage();
                        label = 1;
                    }
                }
            }
            
		}
		catch(DocumentException de) {
			System.err.println(de.getMessage());
		}
		catch(IOException ioe) {
			System.err.println(ioe.getMessage());
		}

		// step 5: we close the document
		document.close();
		
	}

}
