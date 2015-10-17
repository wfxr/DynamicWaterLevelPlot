package Frame;

import Data.Section;

import javax.swing.*;
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

    private GridBagLayout layout;
    private JPanel controlPanel;
    private JPanel statusPanel;

//    private JLabel lblTitle;

    private JButton btnSwitch;
    private JButton btnFrameForward;
    private JButton btnFrameBackward;
    private JButton btnExit;
    private JButton btnStop;

    private Player player;
    private JPanel playerPanel;

    ActionListener exitListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Exit();
        }
    };

    ActionListener switchListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (player.IsStopped()){
                player.Play();
                btnSwitch.setText("暂停");
            }else if(player.IsFinished()) {
                player.Stop();
                player.Play();
                btnSwitch.setText("暂停");
            } else if (player.IsPaused()) {
                btnSwitch.setText("暂停");
                player.Play();
            } else if (player.IsPlaying()) {
                player.Pause();
                btnSwitch.setText("播放");
            }
        }
    };

    ActionListener stopListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            player.Stop();
            btnSwitch.setText("播放");
        }
    };

    ActionListener frameForwardListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            player.FrameForward();
        }
    };
    ActionListener frameBackwardListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            player.FrameBackward();
        }
    };
    PlayerListener playerListener = new PlayerListener() {

        @Override
        public void performOnFinish() {
            btnSwitch.setText("播放");
        }
    };

    private void Exit() {
        System.exit(0);
    }

    public void InitComponents() {
//        lblTitle = new JLabel("水位-时间模拟");
        controlPanel = new JPanel();
        playerPanel = player.createPlayerPanel();
        statusPanel = new JPanel();

        btnSwitch = new JButton("播放");
        btnFrameForward = new JButton("下一帧");
        btnFrameBackward = new JButton("上一帧");
        btnStop = new JButton("停止");
        btnExit = new JButton("退出");

        btnSwitch.addActionListener(switchListener);
        btnFrameForward.addActionListener(frameForwardListener);
        btnFrameBackward.addActionListener(frameBackwardListener);
        btnStop.addActionListener(stopListener);
        btnExit.addActionListener(exitListener);

        player.addPlayerListenerList(playerListener);

        controlPanel.add(btnSwitch);
        controlPanel.add(btnFrameForward);
        controlPanel.add(btnFrameBackward);
        controlPanel.add(btnStop);
        controlPanel.add(btnExit);

        statusPanel.add(new JLabel("Status Area"));

//        this.add(lblTitle);
        this.add(playerPanel);
        this.add(controlPanel);
        // TODO:添加状态区
//        this.add(statusPanel);

        SetLayout();
    }

    public void SetLayout() {
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
        constraints.insets = new Insets(10, 10, 10, 10);
//        layout.setConstraints(lblTitle, constraints);
        // 图表区
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(playerPanel, constraints);
        // 控制区
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(controlPanel, constraints);
        // 状态区
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 0;
        constraints.gridheight = 3;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(statusPanel, constraints);

        this.setLayout(layout);
    }

    public WaterLevelSimulationFrame(TreeMap<Integer, Section> sectionMap) {
        this.setTitle("河道断面水位-时间动态演示");
        this.sectionMap = sectionMap;

        // 将当前断面置为Map中的第一个断面
        // TODO: 目前只有断面2的水位数据，所以初始状态选用2断面以供测试
        section = sectionMap.get(2);
        player = new Player(section.getPoints(), section.getWaterLevelItems());

        // 初始化图形组件
        InitComponents();
    }
}

