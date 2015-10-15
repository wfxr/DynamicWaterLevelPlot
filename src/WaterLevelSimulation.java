import Data.Section;
import Data.SectionMapHelper;
import Frame.WaterLevelSimulationFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class WaterLevelSimulation {
    private TreeMap<Integer, Section> sectionMap;
    private WaterLevelSimulationFrame frame;

    public void ShowFrame(){
        frame = new WaterLevelSimulationFrame(sectionMap);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.Run();
    }

    public WaterLevelSimulation(){
        sectionMap = new TreeMap<>();
    }

    public void LoadData(String sectionsXlsPath, String waterLevelsXlsPath) throws IOException {
        sectionMap = new TreeMap<>();
        SectionMapHelper.LoadMeasurementPointsFromXls(sectionMap, sectionsXlsPath);
        SectionMapHelper.LoadWaterLevelsFromXls(sectionMap, waterLevelsXlsPath);
    }

    public static void main(String[] args) throws IOException {
        WaterLevelSimulation wls = new WaterLevelSimulation();
        wls.LoadData("testdata/Sections.xls", "testdata/WaterLevels.xls");
        wls.ShowFrame();
    }
}
