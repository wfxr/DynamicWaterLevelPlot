package UI;

import Data.Section;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.TreeMap;

/**
 * Created by Wenxuan on 2015/10/14.
 * Email: wenxuan-zhang@outlook.com
 */
public class MainFrame extends JFrame {
    private TreeMap<Integer, Section> sectionMap;
    private Player player;

    private JPanel controlPanel;
    private JPanel statusPanel;
    private JPanel playerPanel;
    private JPanel optionPanel;

    private JButton btnSwitch;
    private JComboBox<Object> cbxSection;

    private ActionListener onSwitch = e -> {
        if (player.IsStopped()) {
            player.Play();
            btnSwitch.setText("暂停");
        } else if (player.IsFinished()) {
            player.Stop();
            player.Play();
            btnSwitch.setText("暂停");
        } else if (player.IsPaused()) {
            btnSwitch.setText("暂停");
            player.Play();
        } else if (player.IsPlaying()) {
            player.Pause();
            btnSwitch.setText("运行");
        }
    };

    private ActionListener onSetSection = e -> {
        Integer sectionId = (Integer) cbxSection.getSelectedItem();
        player.setSection(sectionMap.get(sectionId));
    };

    private ActionListener onStop = e -> {
        player.Stop();
        btnSwitch.setText("运行");
    };

    private ActionListener onExit = e -> System.exit(0);

    public MainFrame(TreeMap<Integer, Section> sectionMap) {
        this.setTitle("河道断面动态水位演示");
        this.sectionMap = sectionMap;

        player = new Player();

        // 初始化图形组件
        InitComponents();

        // 加载断面编号到组合框
        LoadSectionComboBox();

        // 默认展示第一个断面
        if (cbxSection.getItemCount() != 0)
            cbxSection.setSelectedIndex(0);
    }

    public void InitComponents() {
        optionPanel = new JPanel();
        controlPanel = new JPanel();
        playerPanel = player.createPlayerPanel();
        statusPanel = new JPanel();

        cbxSection = new JComboBox<>();

        btnSwitch = new JButton("运行");
        JButton btnFrameForward = new JButton("下一帧");
        JButton btnFrameBackward = new JButton("上一帧");
        JButton btnStop = new JButton("停止");
        JButton btnExit = new JButton("退出");

        cbxSection.addActionListener(onSetSection);
        btnSwitch.addActionListener(onSwitch);
        btnFrameForward.addActionListener(e -> player.FrameForward());
        btnFrameBackward.addActionListener(e -> player.FrameBackward());
        btnStop.addActionListener(onStop);
        btnExit.addActionListener(onExit);

        player.addPlayerListenerList(() -> btnSwitch.setText("运行"));

        optionPanel.add(new JLabel("选择断面："));
        optionPanel.add(cbxSection);

        controlPanel.add(btnSwitch);
        controlPanel.add(btnFrameForward);
        controlPanel.add(btnFrameBackward);
        controlPanel.add(btnStop);
        controlPanel.add(btnExit);

        statusPanel.add(new JLabel("Status Area"));

        this.add(optionPanel);
        this.add(playerPanel);
        this.add(controlPanel);

        SetLayout();
    }

    public void SetLayout() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        // 选项区
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(10, 10, 0, 10);
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(optionPanel, constraints);
        // 图表区
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 10, 10, 10);
        constraints.anchor = GridBagConstraints.CENTER;
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
        constraints.anchor = GridBagConstraints.CENTER;
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

    public void LoadSectionComboBox() {
        ComboBoxModel<Object> model = new DefaultComboBoxModel<>(sectionMap.keySet().toArray());
        cbxSection.setModel(model);
    }
}