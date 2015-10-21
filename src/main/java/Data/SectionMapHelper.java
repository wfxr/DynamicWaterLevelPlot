package Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

/**
 * Created by Wenxuan on 2015/10/14.
 * Email: wenxuan-zhang@outlook.com
 */
public class SectionMapHelper {
    public static void LoadMeasurementPointsFromXls(
            Map<Integer, Section> sectionMap, String path) throws IOException {
        Workbook workbook = new HSSFWorkbook(new FileInputStream(path));
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            int id = (int) row.getCell(0).getNumericCellValue();
            double posX = row.getCell(1).getNumericCellValue();
            double posY = row.getCell(2).getNumericCellValue();
            if (sectionMap.containsKey(id))
                sectionMap.get(id).AddMeasurementPoint(new Point(posX, posY));
            else {
                Section newSection = new Section();
                newSection.AddMeasurementPoint(new Point(posX, posY));
                sectionMap.put(id, newSection);
            }
        }
    }
    public static void LoadWaterLevelsFromXls(
            Map<Integer, Section> sectionMap, String path) throws IOException {
        Workbook workbook = new HSSFWorkbook(new FileInputStream(path));
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet){
            int id = (int) row.getCell(0).getNumericCellValue();
            Date date = row.getCell(1).getDateCellValue();
            double waterLevel = row.getCell(2).getNumericCellValue();
            if (sectionMap.containsKey(id))
                sectionMap.get(id).AddWaterLevel(date, waterLevel);
            else {
                Section newSection = new Section();
                newSection.AddWaterLevel(date, waterLevel);
                sectionMap.put(id, newSection);
            }
        }
    }
}

