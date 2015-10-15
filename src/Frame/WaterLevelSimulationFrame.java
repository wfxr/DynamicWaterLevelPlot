package Frame;

import Data.Section;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by Wenxu on 2015/10/14.
 */
public class WaterLevelSimulationFrame extends JFrame {
    private TreeMap<Integer, Section> sectionMap;
    private Section section;  // 当前断面
    private int waterLevelIndex;  // 当前水位索引

    private GridBagLayout layout;
    private JPanel controlPanel;
    private JPanel statusPanel;
    private Timer timer;

    private JLabel lblTitle;

    private JButton btnSwitch;
    private JButton btnReset;
    private JButton btnExit;

    private ChartPanel chart;

    ActionListener switchListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (IsPlaying()) Pause();
            else Play();
        }
    };


    ActionListener timerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!IsLastFrame())
                AdvanceOneFrame();
            else
                timer.stop();
        }
    };

    ActionListener resetListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ResetSectionStatus();
        }
    };

    ActionListener exitListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Exit();
        }
    };

    private boolean IsLastFrame() {
        return waterLevelIndex == section.getWaterLevelsCount() - 1;
    }

    private void AdvanceOneFrame(){
        SetWaterLevelIndex(waterLevelIndex + 1);
    }

    private boolean IsPlaying() {
        return timer.isRunning();
    }

    private void Play() {
        btnSwitch.setText("Pause");
        timer.start();
    }

    private void Pause() {
        btnSwitch.setText("Continue");
        timer.stop();
    }

    private void ResetSectionStatus() {
        chart.setSectionData(section.getPoints());
        SetWaterLevelIndex(0);
        timer.stop();
        btnSwitch.setText("Play");
    }

    private void Exit() {
        System.exit(0);
    }

    private void SetWaterLevelIndex(int index) {
        waterLevelIndex = index;
        chart.setWaterLevel(section.getWaterLevel(index));
    }

    public void InitComponents() {
        timer = new Timer(30, timerListener);
        lblTitle = new JLabel("Water Level Simulation");
        chart = new ChartPanel();
        controlPanel = new JPanel();
        statusPanel = new JPanel();

        btnSwitch = new JButton("Play");
        btnReset = new JButton("ResetSectionStatus");
        btnExit = new JButton("Exit");

        lblTitle.setFont(new Font("Arial", 0, 18));

        btnSwitch.addActionListener(switchListener);
        btnReset.addActionListener(resetListener);
        btnExit.addActionListener(exitListener);

        controlPanel.add(btnSwitch);
        controlPanel.add(btnReset);
        controlPanel.add(btnExit);

        statusPanel.add(new JLabel("Status Area"));

        this.add(lblTitle);
        this.add(chart);
        this.add(controlPanel);
        this.add(statusPanel);

        SetLayout();
    }

    public void SetLayout(){
        layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        // 标题区
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10,10,10,10);
        layout.setConstraints(lblTitle, constraints);
        // 图表区
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10,10,10,10);
        layout.setConstraints(chart, constraints);
        // 控制区
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10,10,10,10);
        layout.setConstraints(controlPanel, constraints);
        // 状态区
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 0;
        constraints.gridheight = 3;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10,10,10,10);
        layout.setConstraints(statusPanel, constraints);

        this.setLayout(layout);
    }

    public WaterLevelSimulationFrame(TreeMap<Integer, Section> sectionMap) {
        this.sectionMap = sectionMap;

        // 将当前断面置为Map中的第一个断面
        // TODO: 目前只有断面2的水位数据，所以初始状态选用2断面以供测试
        section = sectionMap.get(2);

        // 初始化图形组件
        InitComponents();

        // 重置断面状态
        ResetSectionStatus();
    }
}

