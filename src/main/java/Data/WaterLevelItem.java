package Data;

import java.util.Date;

/**
 * Created by Wenxuan on 2015/10/14.
 * Email: wenxuan-zhang@outlook.com
 */
public class WaterLevelItem {
    public WaterLevelItem(Date time, double waterLevel){
        Time = time;
        WaterLevel = waterLevel;
    }

    public Date Time;
    public double WaterLevel;
}
