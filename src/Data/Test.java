package Data;

import Data.Section;
import Data.SectionMapHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        Map<Integer, Section> sectionMap = new HashMap<>();
        SectionMapHelper.LoadMeasurementPointsFromXls(sectionMap, "E:\\Sections.xls");
        SectionMapHelper.LoadWaterLevelsFromXls(sectionMap, "E:\\WaterLevels.xls");
    }
}
