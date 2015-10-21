package util;

import java.util.*;

/**
 * Created by Wenxuan on 2015/10/14.
 * Email: wenxuan-zhang@outlook.com
 */
public class Section {
    private List<Point> points; // 断面测点高程数据
    private List<WaterLevelItem> waterLevels; // 时间-水位数据

    public Section() {
        this.points = new ArrayList<>();
        this.waterLevels = new ArrayList<>();
    }

    public List<WaterLevelItem> getWaterLevelItems(){
        return waterLevels;
    }

    public void AddMeasurementPoint(Point point) {
        points.add(point);
    }

    public void AddWaterLevel(Date date, double waterLevel) {
        waterLevels.add(new WaterLevelItem(date, waterLevel));
    }

    public List<Point> getPoints() {
        return points;
    }
}
