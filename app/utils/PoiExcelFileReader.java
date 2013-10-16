package utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.Contact;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class PoiExcelFileReader {

	public static void readFile(String fileName) {
		try {
			String fileNameWithPath = "public/upload/" + fileName;
			FileInputStream fileInputStream = new FileInputStream(fileNameWithPath);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet sheet = workbook.getSheetAt (0);
			HSSFRow header = sheet.getRow(0);
			
			System.out.println("Last row number: " + sheet.getLastRowNum());
			
			boolean endOfFile = false;
			//int j = 1;
			//while(!endOfFile){
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
					
					if(name.equals("") & firstName.equals("") & city.equals("")){
						endOfFile = true;
						System.out.println("EOF EOF EOF");
					}
					if(!Contact.alreadyExists(name, firstName, street, city) && !endOfFile) {
						Contact.create(title, name, firstName, email, street, appendix1, appendix2, zipcode, city, phone, belongsTo, yearbook);
					} else {
						System.out.println("Contact " + name + " already exists");
					}
				}
				//j++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String writeFile(List<Contact> contacts) {
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		String filename = "";
		try {
			String now = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
			filename = "SMG_Kontakte_" + now + ".xls";
			FileOutputStream fos = new FileOutputStream(filename);
			
			for(int i = 0; i < contacts.size(); i++) {
				Contact contact = contacts.get(i);
				Row row = sheet.createRow(i);
				Cell title = row.createCell(0);
				title.setCellValue(contact.title);
				Cell firstName = row.createCell(1);
				firstName.setCellValue(contact.firstName);
				Cell name = row.createCell(2);
				name.setCellValue(contact.name);
				Cell street = row.createCell(3);
				street.setCellValue(contact.street);
				Cell app1 = row.createCell(4);
				app1.setCellValue(contact.appendix1);
				Cell app2 = row.createCell(5);
				app2.setCellValue(contact.appendix2);
				Cell zipcode = row.createCell(6);
				zipcode.setCellValue(contact.zipcode);
				Cell city = row.createCell(7);
				city.setCellValue(contact.city);
				Cell country = row.createCell(8);
				country.setCellValue(contact.country);
				Cell phone = row.createCell(9);
				phone.setCellValue(contact.phone);
				Cell group = row.createCell(10);
				group.setCellValue(contact.belongsTo());
				Cell email = row.createCell(11);
				email.setCellValue(contact.email);
			}
			workbook.write(fos);
			fos.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return filename;
	}
}

