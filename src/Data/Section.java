package Data;

import java.util.*;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class Section {
    private TreeSet<MPoint> mPoints; // 断面测点高程数据
    private List<WaterLevelItem> waterGroup; // 时间-水位数据

    public Section() {
        this.mPoints = new TreeSet<>();
        this.waterGroup = new ArrayList<>();
    }

//    public Section(TreeSet<MPoint> points, List<WaterLevelItem> hydrograph) {
//        this.mPoints = points;
//        this.waterGroup = hydrograph;
//    }

    public void AddMeasurementPoint(MPoint point) {
        mPoints.add(point);
    }

    public void AddWaterLevel(Date date, double waterLevel) {
        waterGroup.add(new WaterLevelItem(date, waterLevel));
    }

    public TreeSet<MPoint> getPoints() {
        return mPoints;
    }

//    public void setCurve(TreeSet<MPoint> mPoints) {
//        this.mPoints = mPoints;
//    }

    public List<WaterLevelItem> getWaterGroup() {
        return waterGroup;
    }

//    public void setWaterGroup(List<WaterLevelItem> waterGroup) {
//        this.waterGroup = waterGroup;
//    }
}
