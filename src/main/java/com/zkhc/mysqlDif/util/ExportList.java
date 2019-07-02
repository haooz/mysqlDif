package com.zkhc.mysqlDif.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * 导出差异信息到Excel
 * @author Administrator
 *
 */
public class ExportList {
    
    private Workbook wb;

    public Workbook exportDo(ArrayList<Hashtable<String, String>> diffTabList,
            ArrayList<Hashtable<String, String>> diffColList,
            ArrayList<Hashtable<String, String>> diffIndexList,
            ArrayList<Hashtable<String, String>> diffRoutineList) throws IOException{
     
        String[] diffTabTitle= {"TABLE_NAME1","TABLE_NAME2","TABLE_COMMENT"};
        String[] diffTabTitle1= {"测试库中的表","正式库中的表","表注释"};
        String[] diffColTitle= {"TABLE_NAME","COLUMN_NAME1","COLUMN_NAME2","COLUMN_TYPE","IS_NULLABLE","COLUMN_KEY","EXTRA","COLUMN_COMMENT","COLUMN_DEFAULT"};
        String[] diffColTitle1= {"表名称","测试库中的表列","正式库中的表列","COLUMN_TYPE","IS_NULLABLE","COLUMN_KEY","EXTRA","COLUMN_COMMENT","COLUMN_DEFAULT"};
        String[] diffIndexTitle= {"TABLE_NAME","INDEX_NAME1","INDEX_NAME2","COLUMNS"};
        String[] diffRoutineTitle= {"ROUTINE_NAME1","ROUTINE_NAME2","ROUTINE_TYPE"};
        String[] diffIndexTitle1= {"表名称","测试库中索引","正式库中的索引","索引涉及的列"};
        String[] diffRoutineTitle1= {"测试库中函数过程","正式库中的函数过程","ROUTINE_TYPE"};
        
        wb = new XSSFWorkbook();
        Row row = null;
        Row headerRow = null;
        
        CellStyle paramCellStyle =wb.createCellStyle();
        paramCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        paramCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        paramCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        paramCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        paramCellStyle.setBorderBottom(CellStyle.SOLID_FOREGROUND);
        paramCellStyle.setBorderTop(CellStyle.SOLID_FOREGROUND);
        paramCellStyle.setBorderLeft(CellStyle.SOLID_FOREGROUND);
        paramCellStyle.setBorderRight(CellStyle.SOLID_FOREGROUND);
        
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        
        paramCellStyle.setFont(titleFont);
        
        Sheet sheet1 = wb.createSheet("表信息差异");
        headerRow = sheet1.createRow(0);
        for(int i=0; i<diffTabTitle1.length; i++){
           Cell cell = headerRow.createCell(i);
           cell.setCellStyle(paramCellStyle);
           cell.setCellValue(diffTabTitle1[i]);
        }
        for(int i=0; i<diffTabList.size(); i++){
            row = sheet1.createRow(i+1);
            for(int j=0; j<diffTabTitle.length; j++){
                row.createCell(j).setCellValue(diffTabList.get(i).get(diffTabTitle[j]));
            }
        }
        
        Sheet sheet2 = wb.createSheet("列信息差异");
        headerRow = sheet2.createRow(0);
        for(int i=0; i<diffColTitle1.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(paramCellStyle);
            cell.setCellValue(diffColTitle1[i]);
        }
        for(int i=0; i<diffColList.size(); i++){
            row = sheet2.createRow(i+1);
            for(int j=0; j<diffColTitle.length; j++){
                row.createCell(j).setCellValue(diffColList.get(i).get(diffColTitle[j]));
            }
        }
        
        Sheet sheet3 = wb.createSheet("索引信息差异");
        headerRow = sheet3.createRow(0);
        for(int i=0; i<diffIndexTitle1.length; i++){
           Cell cell = headerRow.createCell(i);
           cell.setCellStyle(paramCellStyle);
           cell.setCellValue(diffIndexTitle1[i]);
        }
        for(int i=0; i<diffIndexList.size(); i++){
            row = sheet3.createRow(i+1);
            for(int j=0; j<diffIndexTitle.length; j++){
                row.createCell(j).setCellValue(diffIndexList.get(i).get(diffIndexTitle[j]));
            }
        }
        
        Sheet sheet4 = wb.createSheet("函数过程信息差异");
        headerRow = sheet4.createRow(0);
        for(int i=0; i<diffRoutineTitle1.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(paramCellStyle);
            cell.setCellValue(diffRoutineTitle1[i]);
        }
        for(int i=0; i<diffRoutineList.size(); i++){
            row = sheet4.createRow(i+1);
            for(int j=0; j<diffRoutineTitle.length; j++){
                row.createCell(j).setCellValue(diffRoutineList.get(i).get(diffRoutineTitle[j]));
            }
        }
        return wb;
    }
}
