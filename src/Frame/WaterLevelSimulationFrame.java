package Frame;

import Data.MPoint;
import Data.Section;
import Data.WaterLevelItem;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class WaterLevelSimulationFrame extends JFrame {
    private TreeMap<Integer, Section> sectionMap;
    private Section currentSection;
    private int currentWaterLevelIndex;

    private Section getCurrentSection() {
        return currentSection;
    }

    private void setCurrentSection(int sectionId) {
        currentSection = sectionMap.get(sectionId);
    }

    private List<WaterLevelItem> currentWaterLevelGroup() {
        return currentSection.getWaterGroup();
    }

    private WaterLevelItem CurrentWaterLevelItem() {
        return currentWaterLevelGroup().get(currentWaterLevelIndex);
    }

    // 前进一帧
    public boolean Forward() {
        if (currentWaterLevelIndex == currentWaterLevelGroup().size() - 1)
            return false;
        ++currentWaterLevelIndex;
        return true;
    }

    // 后退一帧
    public boolean Backward() {
        if (currentWaterLevelIndex == 0)
            return false;
        --currentWaterLevelIndex;
        return true;
    }

    public WaterLevelSimulationFrame(TreeMap<Integer, Section> sectionMap) {
        this.sectionMap = sectionMap;
        Initialize();

        ChartPanel chartPanel = new ChartPanel();
        chartPanel.setSectionPlane(currentSection.getPoints());
        chartPanel.setWaterLevel(CurrentWaterLevelItem().WaterLevel);
        getContentPane().add(chartPanel);
    }

    private void Initialize() {
        // 将当前断面置为Map中的第一个断面
//        currentSection = sectionMap.firstEntry().getValue();
        currentSection = sectionMap.get(2);
        currentWaterLevelIndex = 0;
    }
}

class ChartPanel extends JPanel {
    private SectionPlane sectionPlane;
    private double waterLevel;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(Color.darkGray);
        paintSectionPlane(g2d);
    }

    private void paintSectionPlane(Graphics2D g2d) {
        sectionPlane.setSize(getWidth(), getHeight());
        sectionPlane.setWaterLevel(waterLevel);
//        g2d.fillPolygon(sectionPlane.getPolygon());
        sectionPlane.paint(g2d);
    }

    private void paintWaterLevelLine(Graphics2D g2d) {

    }

    public void setSectionPlane(TreeSet<MPoint> points) {
        this.sectionPlane = new SectionPlane(points);
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }
}

class SectionPlane {
    private TreeSet<MPoint> points;
    private double waterLevel;

    private double left;
    private double bottom;
    private double right;
    private double top;
    private int width;
    private int height;

    private int[] xpoints;
    private int[] ypoints;

    public int ScreenWaterLevel() {
        return screenY(waterLevel);
    }

    public SectionPlane(TreeSet<MPoint> points) {
        setPoints(points);
        setSize(0, 0);
    }

    public void paint(Graphics2D g2d) {
        int y = ScreenWaterLevel();
        Color color = g2d.getColor();
        g2d.setColor(Color.blue);
        g2d.fillRect(0, y, width, y);
        g2d.setColor(color);
        g2d.fillPolygon(xpoints, ypoints, points.size() + 2);
    }

    private void updatePolygon() {
        xpoints = new int[points.size() + 2];
        ypoints = new int[points.size() + 2];
        int i = 0;
        for (MPoint mPoint : points) {
            xpoints[i] = screenX(mPoint.x);
            ypoints[i] = screenY(mPoint.y);
            ++i;
        }
        xpoints[i] = screenX(right);
        ypoints[i] = screenY(bottom);
        xpoints[i + 1] = screenX(left);
        ypoints[i + 1] = screenY(bottom);
    }

    private int screenX(double x) {
        return (int) ((x - left) * width / (right - left));
    }

    private int screenY(double y) {
        return height - (int) ((y - bottom) * height / (top - bottom));
    }

    public void setPoints(TreeSet<MPoint> points) {
        this.points = points;
        updateCorner();
        updatePolygon();
    }

    private void updateCorner() {
        right = top = Double.MIN_VALUE;
        left = bottom = Double.MAX_VALUE;
        for (MPoint mPoint : points) {
            if (right < mPoint.x)
                right = mPoint.x;

            if (left > mPoint.x)
                left = mPoint.x;

            if (top < mPoint.y)
                top = mPoint.y;

            if (bottom > mPoint.y)
                bottom = mPoint.y;
        }
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        updateCorner();
        updatePolygon();
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }
}
