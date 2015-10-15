package Frame;

import Data.MPoint;

import javax.swing.*;
import java.awt.*;
import java.util.TreeSet;

public class ChartPanel extends JComponent {
    private SectionGraph sectionPlane;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintSectionPlane((Graphics2D) g);
    }

    private void paintSectionPlane(Graphics2D g2d) {
        sectionPlane.setSize(getWidth(), getHeight());
        sectionPlane.paint(g2d);
    }

    public void setSectionData(TreeSet<MPoint> points) {
        this.sectionPlane = new SectionGraph(points);
        this.repaint();
    }

    public void setWaterLevel(double waterLevel) {
        sectionPlane.setWaterLevel(waterLevel);
        this.repaint();
    }
}
