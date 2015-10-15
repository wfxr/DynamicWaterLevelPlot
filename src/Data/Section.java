package Data;

import java.util.*;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class Section {
    private TreeSet<MPoint> points; // ������߳�����
    private List<WaterLevelItem> waterLevels; // ʱ��-ˮλ����

    public Section() {
        this.points = new TreeSet<>();
        this.waterLevels = new ArrayList<>();
    }

    public int getWaterLevelsCount() { return waterLevels.size();}

    public void AddMeasurementPoint(MPoint point) {
        points.add(point);
    }

    public void AddWaterLevel(Date date, double waterLevel) {
        waterLevels.add(new WaterLevelItem(date, waterLevel));
    }

    public TreeSet<MPoint> getPoints() {
        return points;
    }

    public double getWaterLevel(int index) {return waterLevels.get(index).WaterLevel; }
}
