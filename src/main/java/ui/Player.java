package ui;

import util.Point;
import util.Section;
import util.WaterLevelItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by Wenxuan on 2015/10/17.
 * Email: wenxuan-zhang@outlook.com
 */
interface PlayerListener {
    void performOnFinish();
}

public class Player {
    private List<Point> sectionPoints;
    private List<WaterLevelItem> waterLevelItems;
    private ListIterator<WaterLevelItem> waterLevelIter;
    private JFreeChart chart;
    private Timer timer;
    private Status status;
    private ArrayList<PlayerListener> playerListenerList = new ArrayList<>();

    private XYSeries sectionSeries;
    private XYSeries waterSeries;

    private TextTitle dateTitle;
    private TextTitle waterLevelTitle;
    ActionListener timerListener = e -> FrameForward();

    public Player() {
        this.timer = new Timer(100, timerListener);
        this.dateTitle = new TextTitle();
        this.waterLevelTitle = new TextTitle();
        this.chart = ChartFactory.createXYAreaChart("", "横向位置(m)", "高程(m)",
                null, PlotOrientation.VERTICAL, true, true, true);
        this.sectionSeries = new XYSeries("断面");
        this.waterSeries = new XYSeries("水位");
        // 日期信息作为主标题
        chart.setTitle(dateTitle);
        dateTitle.setFont(new Font("黑体", Font.PLAIN, 18));

        // 水位信息作为副标题
        waterLevelTitle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        waterLevelTitle.setPadding(0, 0, 0, 60);
        waterLevelTitle.setFont(new Font("黑体", Font.PLAIN, 14));
        chart.addSubtitle(0, waterLevelTitle);

        // 为正确显示水体效果，需要将plot前景色设为不透明
        XYPlot plot = chart.getXYPlot();
        plot.setForegroundAlpha(1.0F);

        // 断面和水体的颜色
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.darkGray);
        renderer.setSeriesPaint(1, new Color(0, 0, 255));

        // 域坐标轴
        ValueAxis xAxis = plot.getDomainAxis();
        xAxis.setTickMarkPaint(Color.black);
        xAxis.setLabelFont(new Font("黑体", Font.PLAIN, 14));

        // 值坐标轴
        ValueAxis yAxis = plot.getRangeAxis(0);
        yAxis.setTickMarkPaint(Color.black);
        yAxis.setLabelFont(new Font("黑体", Font.PLAIN, 14));
        try {
            plot.setRangeAxis(1, (ValueAxis) yAxis.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        // 图例
        chart.removeLegend();
        // 背景
        chart.setBackgroundPaint(null);
    }

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
        playerListenerList.forEach(PlayerListener::performOnFinish);
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
        dateTitle.setText(new SimpleDateFormat("HH:mm:ss yyyy-MM-dd a").format(waterLevelItem.Time));
        waterLevelTitle.setText("当前水位：" + String.format("%8.2f",waterLevelItem.WaterLevel));
    }

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


    public JPanel createPlayerPanel() {
        ChartPanel chartPanel = new ChartPanel(chart, true, true, true, false, true);
        chartPanel.setMouseZoomable(false);
        return chartPanel;
    }

    public void setSection(Section section) {
        timer.stop();

        this.sectionPoints = section.getPoints();
        this.waterLevelItems = section.getWaterLevelItems();
        this.waterLevelIter = waterLevelItems.listIterator();
        this.status = Status.Stopped;

        updateChart();

        // 默认显示第一帧的画面
        FrameForward();
    }

    private void updateChart() {
        waterLevelTitle.setText("无水位数据");
        dateTitle.setText("无日期数据");

        // 更新数据集
        updateDataset();

        XYPlot plot = chart.getXYPlot();

        // 更新域(x)坐标轴
        ValueAxis xAxis = plot.getDomainAxis();
        xAxis.setLowerBound(sectionSeries.getMinX());
        xAxis.setUpperBound(sectionSeries.getMaxX());

        // 更新值(y)坐标轴
        ValueAxis yAxis0 = plot.getRangeAxis(0);
        yAxis0.setLowerBound(sectionSeries.getMinY());
        yAxis0.setUpperBound(sectionSeries.getMaxY());
        ValueAxis yAxis1 = plot.getRangeAxis(1);
        yAxis1.setLowerBound(sectionSeries.getMinY());
        yAxis1.setUpperBound(sectionSeries.getMaxY());
    }

    private void updateDataset() {
        sectionSeries.clear();
        for (Point point : sectionPoints)
            sectionSeries.add(point.x, point.y);

        waterSeries.clear();
        waterSeries.add(sectionSeries.getMinX(), 0);
        waterSeries.add(sectionSeries.getMaxX(), 0);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sectionSeries);
        dataset.addSeries(waterSeries);
        chart.getXYPlot().setDataset(dataset);
    }

    public enum Status {
        Playing,
        Paused,
        Stopped,
        Finished
    }
}