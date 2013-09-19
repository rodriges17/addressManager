package utils;

import java.io.*;
import java.util.Iterator;

import models.Contact;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


public class PoiExcelFileReader {

	public static void readFile() {
		try {
			FileInputStream fileInputStream = new FileInputStream("data.xls");
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet sheet = workbook.getSheetAt (0);
			HSSFRow header = sheet.getRow(0);

			for(int j = 1; j < 633; j++){
				System.out.println("row: " + j);
				HSSFRow row = sheet.getRow(j);
				if(row!=null){
					
					String title = row.getCell(1).getRichStringCellValue().getString();
					String firstName = row.getCell(2).getRichStringCellValue().getString();
					String name = row.getCell(3).getRichStringCellValue().getString();
					
					String nr = "";
					if(row.getCell(5).getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
						Double nrDouble = row.getCell(5).getNumericCellValue();
						nr = nrDouble.toString();
						nr = nr.substring(0, nr.length()-2);
					}
					if(row.getCell(5).getCellType()==HSSFCell.CELL_TYPE_STRING){
						nr = row.getCell(5).getRichStringCellValue().getString();
					}
					
					String street = row.getCell(4).getRichStringCellValue().getString() + " " + nr;
					String appendix1 = row.getCell(6).getRichStringCellValue().getString();
					String appendix2 = row.getCell(7).getRichStringCellValue().getString();
					
					String zipcode = "";
					if(row.getCell(8).getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
						Double zipcodeDouble = row.getCell(8).getNumericCellValue();
						zipcode = zipcodeDouble.toString();
						zipcode = zipcode.substring(0, zipcode.length()-2);
					}
					if(row.getCell(8).getCellType()==HSSFCell.CELL_TYPE_STRING){
						zipcode = row.getCell(8).getRichStringCellValue().getString();
					}
					
					String city = row.getCell(9).getRichStringCellValue().getString();
					String country = row.getCell(10).getRichStringCellValue().getString();
					String phone = row.getCell(12).getRichStringCellValue().getString();
					String belongsTo = row.getCell(13).getRichStringCellValue().getString();
					String yearbook = row.getCell(14).getRichStringCellValue().getString();
					String email = row.getCell(16).getRichStringCellValue().getString();

					Contact.create(title, name, firstName, email, street, appendix1, appendix2, zipcode, city, phone, belongsTo, yearbook);
					
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

