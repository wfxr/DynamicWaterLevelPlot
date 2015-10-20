package Demo;

import Data.Section;
import Data.SectionMapHelper;
import UI.MainFrame;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.TreeMap;

/**
 * Created by Wenxuan on 2015/10/14.
 */
public class SectionDynamicWaterLevelPlot {
    private TreeMap<Integer, Section> sectionPoints;
    private MainFrame frame;

    public SectionDynamicWaterLevelPlot() {
        sectionPoints = new TreeMap<>();
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
        initGlobalFontSetting(new Font("微软雅黑", Font.PLAIN, 16));
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SectionDynamicWaterLevelPlot wls = new SectionDynamicWaterLevelPlot();
        wls.LoadData("testdata/Sections.xls", "testdata/WaterLevels.xls");
        wls.ShowFrame();
    }

    public void ShowFrame() {
        frame = new MainFrame(sectionPoints);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void LoadData(String sectionsXlsPath, String waterLevelsXlsPath) throws IOException {
        sectionPoints = new TreeMap<>();
        SectionMapHelper.LoadMeasurementPointsFromXls(sectionPoints, sectionsXlsPath);
        SectionMapHelper.LoadWaterLevelsFromXls(sectionPoints, waterLevelsXlsPath);
    }
}
