package Frame;

import Data.MPoint;
import Data.Section;
import Data.WaterLevelItem;
import org.jfree.chart.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;

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

    private XYSeries sectionSeries;
    private XYSeries waterSeries;

    private TextTitle dateTitle;
    private TextTitle waterLevelTitle;

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
        dateTitle.setText(new SimpleDateFormat("HH:mm:ss yyyy-MM-dd a").format(waterLevelItem.Time));
        waterLevelTitle.setText("当前水位：" + String.format("%8.2f",waterLevelItem.WaterLevel));
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


    public JPanel createPlayerPanel() {
        ChartPanel chartPanel = new ChartPanel(chart, true, true, true, false, true);
        chartPanel.setMouseZoomable(false);
        return chartPanel;
    }

    public Player() {
        this.timer = new Timer(100, timerListener);
        this.dateTitle = new TextTitle();
        this.waterLevelTitle = new TextTitle();
        this.chart = ChartFactory.createXYAreaChart("", "横向位置(m)", "高程(m)",
                null, PlotOrientation.VERTICAL, true, true, true);
        // 日期信息作为主标题
        chart.setTitle(dateTitle);

        // 水位信息作为副标题
        waterLevelTitle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        waterLevelTitle.setPadding(0,0,0,60);
        waterLevelTitle.setFont(new Font("黑体", Font.PLAIN, 14));
        chart.addSubtitle(0, waterLevelTitle);
    }

    public void setSection(Section section) {
        timer.stop();

        this.sectionPoints = section.getPoints();
        this.waterLevelItems = section.getWaterLevelItems();
        this.waterLevelIter = waterLevelItems.listIterator();
        this.status = Status.Stopped;

        this.sectionSeries = new XYSeries("断面");
        this.waterSeries = new XYSeries("水位");

        updateChart();

        // 默认显示第一帧的画面
        FrameForward();
    }

    private void updateChart() {
        waterLevelTitle.setText("无水位数据");
        dateTitle.setText("无日期数据");

        XYPlot plot = chart.getXYPlot();
        plot.setDataset(createDataset());
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
//        renderer.setSeriesPaint(0, new Color(132, 57, 0));
        renderer.setSeriesPaint(0, Color.darkGray);
        renderer.setSeriesPaint(1, new Color(0, 0, 255));

        // 标题和图例的字体
        chart.getTitle().setFont(new Font("黑体", Font.BOLD, 18));

        // 图例
        chart.removeLegend();
        // 背景
        chart.setBackgroundPaint(null);
    }

    private XYDataset createDataset() {
        sectionSeries.clear();
        waterSeries.clear();
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