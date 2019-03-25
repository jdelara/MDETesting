package anatlyzer.testing.comparison.xmi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSaver {
	private Workbook workbook;
	private Sheet absoluteComparison;
	private String outPath;
	private String fileName;
	private int summaryRow;
	
	public ExcelSaver(int size, String path, String fileName) {
		this.fileName = fileName;
		this.outPath  = path;
		workbook = new XSSFWorkbook();
		CellStyle headerstyle = workbook.createCellStyle(); 
		headerstyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex()); 
		headerstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		CellStyle datastyle   = workbook.createCellStyle(); 
		datastyle.setWrapText(true);  
		datastyle.setDataFormat(workbook.createDataFormat().getFormat("0"));
		CellStyle errorstyle  = workbook.createCellStyle(); 
		errorstyle.setWrapText(true); 
		errorstyle.setFillForegroundColor(IndexedColors.RED.getIndex()); 
		errorstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		this.absoluteComparison = createSheet(workbook, "absolute", headerstyle, size);
		//this.percentageComparison = createSheet(workbook, "percentage", headerstyle, size);
	}
	
	public Sheet getAbsolute() {
		return this.absoluteComparison;
	}
	
	private Sheet createSheet (Workbook workbook, String nameSheet, CellStyle headerstyle, int size) {
		Sheet sheet = workbook.createSheet(nameSheet);
		Row  row = sheet.createRow(0);
		createHeaderRow(headerstyle, size, sheet, row);
		for (int j = 1; j < size+1; j++) {
			row = sheet.createRow(j);
			Cell cell = row.createCell(0);
			cell.setCellValue("m"+j);
			cell.setCellStyle(headerstyle);
		}
		this.summaryRow = size+1;
		row = sheet.createRow(size+1);
		Cell cell = row.createCell(0);
		cell.setCellValue("Num. diff models");
		cell.setCellStyle(headerstyle);
		
		row = sheet.createRow(size+2);
		cell = row.createCell(0);
		cell.setCellValue("Clusters");
		cell.setCellStyle(headerstyle);

		row = sheet.createRow(size+3);
		cell = row.createCell(0);
		cell.setCellValue("Min Size");
		cell.setCellStyle(headerstyle);
		
		row = sheet.createRow(size+4);
		cell = row.createCell(0);
		cell.setCellValue("Max Size");
		cell.setCellStyle(headerstyle);
		
		row = sheet.createRow(size+5);
		cell = row.createCell(0);
		cell.setCellValue("Avg Size");
		cell.setCellStyle(headerstyle);
		
		return sheet;
	}

	private void createHeaderRow(CellStyle headerstyle, int size, Sheet sheet, Row row) {
		Cell cell;
		for (int i=0; i<size+1; i++) {
			sheet.setColumnWidth(i, 1800);
			cell = row.createCell(i);
			if (i>0)
				cell.setCellValue("m"+i);      
			cell.setCellStyle(headerstyle);
		}		
		sheet.setColumnWidth(size+1, 1800);
		cell = row.createCell(size+1);
		cell.setCellValue("Size"); 
		cell.setCellStyle(headerstyle);
	}

	
	public void closeExcelWorkbook() {
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(this.outPath + File.separator + this.fileName);
			workbook.write(outputStream);
			workbook.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	public void writeSummary(List<Set<Integer>> diffModels, int minSize, int maxSize, double avgSize) {
		Row r = this.absoluteComparison.getRow(this.summaryRow);
		Cell c = r.createCell(1);
		c.setCellValue(diffModels.size());
		
		r = this.absoluteComparison.getRow(this.summaryRow+1);
		c = r.createCell(1);
		c.setCellValue(diffModels.toString());
		
		r = this.absoluteComparison.getRow(this.summaryRow+2);
		c = r.createCell(1);
		c.setCellValue(minSize);
		
		r = this.absoluteComparison.getRow(this.summaryRow+3);
		c = r.createCell(1);
		c.setCellValue(maxSize);
		
		r = this.absoluteComparison.getRow(this.summaryRow+4);
		c = r.createCell(1);
		c.setCellType(CellType.NUMERIC);
		c.setCellValue(avgSize);
	}
}
