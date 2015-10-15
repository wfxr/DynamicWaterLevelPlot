package Frame;

import Data.MPoint;
import Data.Section;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class WaterLevelSimulationFrame extends JFrame {
    private TreeMap<Integer, Section> sectionMap;
    private Section section;  // ��ǰ����
    private int waterLevelIndex;  // ��ǰˮλ����
    private ChartPanel chartPanel;

    public void Run(){
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ++waterLevelIndex;
                if(waterLevelIndex < section.getWaterLevelsCount())
                    chartPanel.setWaterLevel(section.getWaterLevel(waterLevelIndex));
            }
        };

        Timer timer = new Timer(30, listener);
        timer.start();
    }

    public WaterLevelSimulationFrame(TreeMap<Integer, Section> sectionMap) {
        this.sectionMap = sectionMap;
        Initialize();

        chartPanel = new ChartPanel();
        chartPanel.setSectionPlane(section.getPoints());
        getContentPane().add(chartPanel);
    }

    private void Initialize() {
        // ����ǰ������ΪMap�еĵ�һ������
//        section = sectionMap.firstEntry().getValue();
        // TODO: Ŀǰֻ�ж���2��ˮλ���ݣ����Գ�ʼ״̬ѡ��2�����Թ�����
        section = sectionMap.get(2);
        waterLevelIndex = -1;
    }
}

class ChartPanel extends JPanel {
    private SectionGraph sectionPlane;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintSectionPlane((Graphics2D)g);
    }

    private void paintSectionPlane(Graphics2D g2d) {
        sectionPlane.setSize(getWidth(), getHeight());
        sectionPlane.paint(g2d);
    }

    public void setSectionPlane(TreeSet<MPoint> points) {
        this.sectionPlane = new SectionGraph(points);
        this.repaint();
    }

    public void setWaterLevel(double waterLevel) {
        sectionPlane.setWaterLevel(waterLevel);
        this.repaint();
    }
}

class SectionGraph {
    private TreeSet<MPoint> points; // ����������
    private double waterLevel;      // ˮλֵ

    private Color sectionColor; // ������ɫ
    private Color waterColor;   // ˮ����ɫ

    private double left;   // ԭʼ����������߽�
    private double bottom; // ԭʼ���������±߽�
    private double right;  // ԭʼ���������ұ߽�
    private double top;    // ԭʼ���������ϱ߽�

    private int width;  // ͼ����Ļ�ߴ�
    private int height; // ͼ����Ļ�߶�

    private int[] xpoints; // ������Ļ����xϵ��
    private int[] ypoints; // ������Ļ����yϵ��

    public void setSectionColor(Color color){
        this.sectionColor = color;
    }

    public void setWaterColor(Color color){
        this.waterColor = color;
    }

    // ����ˮλ����ĻY����
    public int ScreenWaterLevel() {
        return screenY(waterLevel);
    }

    public SectionGraph(TreeSet<MPoint> points) {
        setSectionColor(Color.darkGray);
        setWaterColor(Color.blue);
        this.points = points;
        setSize(0, 0);
    }

    public void paint(Graphics2D g2d) {
        Color color = g2d.getColor();

        int y = ScreenWaterLevel();
        g2d.setColor(waterColor);
        g2d.fillRect(0, y, width, y);

        g2d.setColor(sectionColor);
        g2d.fillPolygon(xpoints, ypoints, points.size() + 2);

        g2d.setColor(color);
    }

    private void updateScreenSectionData() {
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

    private void updateOriginalBoundaries() {
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
        updateOriginalBoundaries();
        updateScreenSectionData();
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }
}
