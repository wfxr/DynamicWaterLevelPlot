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
        SectionCurve curve = new SectionCurve(currentSection.getPoints());
        chartPanel.setCurve(curve);
        getContentPane().add(chartPanel);
    }

    private void Initialize() {
        // 将当前断面置为Map中的第一个断面
        currentSection = sectionMap.firstEntry().getValue();
    }
}

class ChartPanel extends JPanel {
    private SectionCurve curve;
    private double waterLevel;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(Color.darkGray);
        paintSectionCurve(g2d);
    }

    private void paintSectionCurve(Graphics2D g2d) {
        curve.setSize(getWidth(), getHeight());
        g2d.drawPolyline(curve.getXSeries(), curve.getYSeries(), curve.getPointsCount());
    }

    private void paintWaterLevelLine(Graphics2D g2d){


    }

    public void setCurve(SectionCurve curve) {
        this.curve = curve;
    }
}

class SectionCurve {
    private TreeSet<MPoint> points;
    private int[][] screenXy; // screenXy[0]为x坐标构成的数组，screenXy[1]为y坐标构成的数组
    private double left;
    private double bottom;
    private double right;
    private double top;
    private int width;
    private int height;

    public int[] getXSeries() {
        return screenXy[0];
    }

    public int[] getYSeries() {
        return screenXy[1];
    }

    public SectionCurve(TreeSet<MPoint> points) {
        setPoints(points);
        setSize(0, 0);
    }

    private void updateScreenXy() {
        int i = 0;
        for (MPoint mPoint : points) {
            screenXy[0][i] = screenX(mPoint.x, width);
            screenXy[1][i] = screenY(mPoint.y, height);
            ++i;
        }
    }

    private int screenX(double x, int width) {
        return (int) ((x - left) * width / (right - left));
    }

    private int screenY(double y, int height) {
        return height - (int) ((y - bottom) * height / (top - bottom));
    }

    public int getPointsCount() {
        return points.size();
    }

    public void setPoints(TreeSet<MPoint> points) {
        this.points = points;
        screenXy = new int[2][points.size()];
        updateCorner();
        updateScreenXy();
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
        updateScreenXy();
    }
}
