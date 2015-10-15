package Frame;

import Data.MPoint;
import javafx.geometry.Rectangle2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.TreeSet;

public class SectionGraph {
    private double[] xSeries;   // 断面数据x系列
    private double[] ySeries;   // 断面数据y系列
    private double waterLevel;  // 水位值

    private int sectionDataCount;  // 断面数据个数

    private Color sectionColor; // 断面颜色
    private Color waterColor;   // 水体颜色
    private Color axisColor;    // 坐标轴颜色

    private Rectangle2D bounds; // 原始数据边界

    private int sectionWidth;  // 断面宽度
    private int sectionHeight; // 断面高度

    private int width;      // 图表宽度
    private int height;     // 图表高度

    private Polygon section;
    private Polygon water;
    private Line2D xAxis;

    public SectionGraph(TreeSet<MPoint> points) {
        section = new Polygon();
        water = new Polygon();
        xAxis = new Line2D.Double();


        axisColor = Color.cyan;
        sectionColor = Color.darkGray;
        waterColor = Color.blue;

        setSectionData(points);
    }

    public void setSectionData(TreeSet<MPoint> points) {
        // 将points中的数据存储到xSeries和ySeries
        sectionDataCount = points.size();
        xSeries = new double[sectionDataCount];
        ySeries = new double[sectionDataCount];
        int i = 0;
        for (MPoint mPoint : points) {
            xSeries[i] = mPoint.x;
            ySeries[i] = mPoint.y;
            ++i;
        }

        updateBounds();
        updateSection();
        updateWater();
        updateAxis();
    }

    private void updateAxis() {
        int x1 = screenX(bounds.getMinX());
        int x2 = screenX(bounds.getMaxX());
        int y = screenY(bounds.getMinY());
        xAxis.setLine(x1, y, x2, y);
    }

    public void paint(Graphics2D g2d) {
        Color color = g2d.getColor();


        g2d.setColor(waterColor);
        g2d.fill(water);

        g2d.setColor(sectionColor);
        g2d.fill(section);

        paintAxis(g2d);

        g2d.setColor(color);
    }

    private void paintAxis(Graphics2D g2d){
        Color color = g2d.getColor();
        Stroke stroke =  g2d.getStroke();

        g2d.setColor(axisColor);
        g2d.setStroke(new BasicStroke(10));
        g2d.draw(xAxis);

        g2d.setColor(color);
        g2d.setStroke(stroke);
    }

    private void updateSection() {
        int npoint = sectionDataCount + 2;  // 断面多边形顶点数
        int[] xpoints = new int[npoint];
        int[] ypoints = new int[npoint];
        for (int i = 0; i < npoint - 2; ++i) {
            xpoints[i] = screenX(xSeries[i]);
            ypoints[i] = screenY(ySeries[i]);
        }

        // 右下角点
        xpoints[npoint - 2] = screenX(bounds.getMaxX());
        ypoints[npoint - 2] = screenY(bounds.getMinY());
        // 左下角点
        xpoints[npoint - 1] = screenX(bounds.getMinX());
        ypoints[npoint - 1] = screenY(bounds.getMinY());

        section.npoints = npoint;
        section.xpoints = xpoints;
        section.ypoints = ypoints;
    }

    private void updateWater() {
        int left = screenX(bounds.getMinX());
        int right = screenX(bounds.getMaxX());
        int top = screenY(waterLevel);
        int bottom = screenY(bounds.getMinY());
        int[] xpoints = {left, right, right, left};
        int[] ypoints = {top, top, bottom, bottom};
        water.npoints = 4;
        water.xpoints = xpoints;
        water.ypoints = ypoints;
    }

    private int screenX(double x) {
        return (int) (normalizedX(x) * sectionWidth);
    }

    private int screenY(double y) {
        return (int) (normalizedY(y) * sectionHeight);
    }

    private double normalizedX(double x) {
        return ((x - bounds.getMinX()) / bounds.getWidth());
    }

    private double normalizedY(double y) {
        return 1 - (y - bounds.getMinY()) / bounds.getHeight();
    }

    // 更新断面数据的坐标边界
    private void updateBounds() {
        double min_x, min_y, max_x, max_y;
        max_x = max_y = Double.MIN_VALUE;
        min_x = min_y = Double.MAX_VALUE;
        for (int i = 0; i < sectionDataCount; ++i) {
            if (max_x < xSeries[i]) max_x = xSeries[i];
            if (min_x > xSeries[i]) min_x = xSeries[i];
            if (max_y < ySeries[i]) max_y = ySeries[i];
            if (min_y > ySeries[i]) min_y = ySeries[i];
        }
        bounds = new Rectangle2D(min_x, min_y, max_x - min_x, max_y - min_y);
    }

    public void setSize(int width, int height) {
        this.height = height;
        this.width = width;

        this.sectionWidth = width - 50;
        this.sectionHeight = height - 50;

        updateSection();
        updateWater();
        updateAxis();
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
        updateWater();
    }
}
