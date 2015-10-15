package Frame;

import Data.MPoint;
import javafx.geometry.Rectangle2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.TreeSet;

public class SectionGraph {
    private double[] xSeries;   // ��������xϵ��
    private double[] ySeries;   // ��������yϵ��
    private double waterLevel;  // ˮλֵ

    private int sectionDataCount;  // �������ݸ���

    private Color sectionColor; // ������ɫ
    private Color waterColor;   // ˮ����ɫ
    private Color axisColor;    // ��������ɫ

    private Rectangle2D bounds; // ԭʼ���ݱ߽�

    private int sectionWidth;  // ������
    private int sectionHeight; // ����߶�

    private int width;      // ͼ����
    private int height;     // ͼ��߶�

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
        // ��points�е����ݴ洢��xSeries��ySeries
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
        int npoint = sectionDataCount + 2;  // �������ζ�����
        int[] xpoints = new int[npoint];
        int[] ypoints = new int[npoint];
        for (int i = 0; i < npoint - 2; ++i) {
            xpoints[i] = screenX(xSeries[i]);
            ypoints[i] = screenY(ySeries[i]);
        }

        // ���½ǵ�
        xpoints[npoint - 2] = screenX(bounds.getMaxX());
        ypoints[npoint - 2] = screenY(bounds.getMinY());
        // ���½ǵ�
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

    // ���¶������ݵ�����߽�
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
