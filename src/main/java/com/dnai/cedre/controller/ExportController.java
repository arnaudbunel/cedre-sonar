package com.dnai.cedre.controller;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.ExportEtudeMdl;
import com.dnai.cedre.service.ExportEtudeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ExportController {

	@Autowired
	private ExportEtudeService exportEtudeService;

	private static final String FILE_NAME = "/home/arnaud/Documents/workspaces/cedre/etude-capgemini/export.xlsx";
	
	@GetMapping(value = "exportetude")
	public void exportetude() {
		try {
			List<ExportEtudeMdl> listeExportEtudeMdl = exportEtudeService.exportEtude();

			String titres[] = { "Nom du client", "Adresse", "Latitude", "Longitude", "Volume semaine 1",
					"Volume semaine 2", "Volume semaine 3", "Volume semaine 4" };

			Workbook workbook = initWorkbook("Clients", titres);
			Sheet sheet = workbook.getSheetAt(0);

			int rowNum = 1;
			for (ExportEtudeMdl exportEtudeMdl : listeExportEtudeMdl) {
				Row row = sheet.createRow(rowNum);

				Cell cellNom = row.createCell(0);
				cellNom.setCellValue(exportEtudeMdl.getClient());

				Cell cellAdresse = row.createCell(1);
				cellAdresse.setCellValue(exportEtudeMdl.getAdresse());

				Cell cellLatitude = row.createCell(2);
				cellLatitude.setCellValue(exportEtudeMdl.getLatitude());

				Cell cellLongitude = row.createCell(3);
				cellLongitude.setCellValue(exportEtudeMdl.getLongitude());

				Cell cellSem1 = row.createCell(4);
				cellSem1.setCellValue(exportEtudeMdl.getVsem1());

				Cell cellSem2 = row.createCell(5);
				cellSem2.setCellValue(exportEtudeMdl.getVsem2());

				Cell cellSem3 = row.createCell(6);
				cellSem3.setCellValue(exportEtudeMdl.getVsem3());

				Cell cellSem4 = row.createCell(7);
				cellSem4.setCellValue(exportEtudeMdl.getVsem4());
				rowNum ++;
			}

			FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
			workbook.write(outputStream);
			workbook.close();

		} catch (Exception e) {
			log.error("exportetude " + e.toString());
		}
	}

	private Workbook initWorkbook(String sheetName, String titres[]) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(sheetName);

		// header
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 10);

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setWrapText(true);

		// Create a Row
		Row headerRow = sheet.createRow(0);
		headerRow.setHeightInPoints((3 * sheet.getDefaultRowHeightInPoints()));

		int idxTitre = 0;
		for (String titre : titres) {
			Cell cellTitre = headerRow.createCell(idxTitre);
			cellTitre.setCellValue(titre);
			cellTitre.setCellStyle(headerCellStyle);
			idxTitre++;
		}
		return workbook;
	}
}
