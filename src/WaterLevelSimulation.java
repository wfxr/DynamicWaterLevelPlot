import Data.Section;
import Data.SectionMapHelper;
import Frame.WaterLevelSimulationFrame;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.TreeMap;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class WaterLevelSimulation {
    private TreeMap<Integer, Section> sectionPoints;
    private WaterLevelSimulationFrame frame;

    public void ShowFrame() {
        frame = new WaterLevelSimulationFrame(sectionPoints);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public WaterLevelSimulation() {
        sectionPoints = new TreeMap<>();
    }

    public void LoadData(String sectionsXlsPath, String waterLevelsXlsPath) throws IOException {
        sectionPoints = new TreeMap<>();
        SectionMapHelper.LoadMeasurementPointsFromXls(sectionPoints, sectionsXlsPath);
        SectionMapHelper.LoadWaterLevelsFromXls(sectionPoints, waterLevelsXlsPath);
    }

    public static void initGlobalFontSetting(Font fnt) {
        FontUIResource fontRes = new FontUIResource(fnt);
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource)
                UIManager.put(key, fontRes);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        // 设置显示风格
        initGlobalFontSetting(new Font("宋体", Font.PLAIN, 14));
        UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());

        WaterLevelSimulation wls = new WaterLevelSimulation();
        wls.LoadData("testdata/Sections.xls", "testdata/WaterLevels.xls");
        wls.ShowFrame();
    }
}
