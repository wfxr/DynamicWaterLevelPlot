package Frame;

import Data.MPoint;
import Data.WaterLevelItem;
import org.jfree.chart.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


interface PlayerListener {
    void performOnFinish();
}

public class Player {
    private List<MPoint> sectionPoints;
    private List<WaterLevelItem> waterLevelItems;
    private ListIterator<WaterLevelItem> waterLevelIter;
    private JFreeChart chart;
    private Timer timer;
    private Status status;
    private ArrayList<PlayerListener> playerListenerList = new ArrayList<>();

    public boolean IsStopped() {
        return status == Status.Stopped;
    }

    public boolean IsPlaying() {
        return status == Status.Playing;
    }

    public boolean IsPaused() {
        return status == Status.Paused;
    }

    public boolean IsFinished() {
        return status == Status.Finished;
    }

    public void addPlayerListenerList(PlayerListener listener) {
        playerListenerList.add(listener);
    }

    public void onFinished() {
        for (PlayerListener listener : playerListenerList)
            listener.performOnFinish();
    }

    public enum Status {
        Playing,
        Paused,
        Stopped,
        Finished
    }

    private void setFinished() {
        onFinished();
        status = Status.Finished;
    }

    public void FrameForward() {
        if (waterLevelIter.hasNext())
            UpdateWaterLevel(waterLevelIter.next());
        else {
            timer.stop();
            setFinished();
        }
    }

    public void FrameBackward() {
        if (waterLevelIter.hasPrevious())
            UpdateWaterLevel(waterLevelIter.previous());
        else {
            timer.stop();
            setFinished();
        }
    }

    private void UpdateWaterLevel(WaterLevelItem waterLevelItem) {
        waterSeries.update(sectionSeries.getMinX(), waterLevelItem.WaterLevel);
        waterSeries.update(sectionSeries.getMaxX(), waterLevelItem.WaterLevel);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd a");
        chart.getTitle().setText(dateFormat.format(waterLevelItem.Time));
    }

    ActionListener timerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            FrameForward();
        }
    };

    public void Play() {
        if (status == Status.Finished)
            Stop();

        timer.start();
        status = Status.Playing;
    }

    public void Pause() {
        timer.stop();
        status = Status.Paused;
    }

    public void Stop() {
        timer.stop();
        waterLevelIter = waterLevelItems.listIterator();
        FrameForward();
        status = Status.Stopped;
    }

    private XYSeries sectionSeries;
    private XYSeries waterSeries;

    public JPanel createPlayerPanel() {
        ChartPanel chartPanel = new ChartPanel(chart, true, true, true, false, true);
//        chartPanel.add(new JSlider());
        return chartPanel;
    }

    public Player(List<MPoint> sectionPoints, List<WaterLevelItem> waterLevelItems) {
        this.sectionPoints = sectionPoints;
        this.waterLevelItems = waterLevelItems;
        this.waterLevelIter = waterLevelItems.listIterator();
        this.status = Status.Stopped;

        this.timer = new Timer(100, timerListener);
        this.sectionSeries = new XYSeries("断面");
        this.waterSeries = new XYSeries("水位");

        this.chart = createChart(createDataset());

        // 默认显示第一帧的画面
        FrameForward();
    }

    private JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYAreaChart(
                "断面水位-关系图", "横向位置(m)", "高程(m)", dataset, PlotOrientation.VERTICAL, true, true, true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setForegroundAlpha(1.0F);

        // 域(x)坐标轴
        ValueAxis xAxis = plot.getDomainAxis();
        xAxis.setTickMarkPaint(Color.black);
        xAxis.setLowerBound(sectionSeries.getMinX());
        xAxis.setUpperBound(sectionSeries.getMaxX());
        xAxis.setLabelFont(new Font("黑体", Font.PLAIN, 14));

        // 值(y)坐标轴
        ValueAxis yAxis_left = plot.getRangeAxis();
        yAxis_left.setTickMarkPaint(Color.black);
        yAxis_left.setLowerBound(sectionSeries.getMinY());
        yAxis_left.setUpperBound(sectionSeries.getMaxY());
        yAxis_left.setLabelFont(new Font("黑体", Font.PLAIN, 14));
        try {
            ValueAxis yAxis_right = (ValueAxis) yAxis_left.clone();
            plot.setRangeAxis(1, yAxis_right);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        // 断面和水体的颜色
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.darkGray);
        renderer.setSeriesPaint(1, Color.blue);

        // 标题和图例的字体
        chart.getTitle().setFont(new Font("黑体", Font.BOLD, 18));

        // 不显示图例
        chart.removeLegend();

        chart.setBackgroundPaint(null);

        return chart;
    }

    private XYDataset createDataset() {
        for (MPoint point : sectionPoints)
            sectionSeries.add(point.x, point.y);
        waterSeries.add(sectionSeries.getMinX(), 0);
        waterSeries.add(sectionSeries.getMaxX(), 0);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sectionSeries);
        dataset.addSeries(waterSeries);
        dataset.setIntervalWidth(0.0D);
        return dataset;
    }
}