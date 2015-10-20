package Data;

import java.util.Date;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class WaterLevelItem {
    public WaterLevelItem(Date time, double waterLevel){
        Time = time;
        WaterLevel = waterLevel;
    }

    public Date Time;
    public double WaterLevel;
}
