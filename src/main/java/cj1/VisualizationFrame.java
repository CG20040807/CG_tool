package cj1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.nio.file.Path;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class VisualizationFrame extends JFrame {

    public VisualizationFrame(List<WellRecord> records, String summaryText, Path imagePath) {
        super("测井结果可视化");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        VisualizationPanel chartPanel = new VisualizationPanel(records);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JTextArea summaryArea = new JTextArea(
            summaryText
                + System.lineSeparator()
                + "图例：蓝线是孔隙度，橙线是泥质含量，绿线是含油饱和度。"
                + System.lineSeparator()
                + "淡绿色背景表示符合条件的好油层。"
                + System.lineSeparator()
                + "图片路径: " + imagePath.toAbsolutePath()
        );
        summaryArea.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setEditable(false);
        summaryArea.setOpaque(false);
        summaryArea.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(960, 170));
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        add(chartPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }
}
