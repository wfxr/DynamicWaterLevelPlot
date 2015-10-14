package Data;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class MPoint implements Comparable<MPoint> {
    public MPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double x;
    public double y;

    @Override
    public int compareTo(MPoint o) {
        return this.x < o.x ? -1 : (this.x == o.y ? 0 : 1);
    }
}
