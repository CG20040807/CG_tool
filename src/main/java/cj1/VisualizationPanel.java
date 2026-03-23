package cj1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;

public class VisualizationPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(245, 248, 252);
    private static final Color CHART_BACKGROUND = Color.WHITE;
    private static final Color GRID_COLOR = new Color(220, 227, 236);
    private static final Color TEXT_COLOR = new Color(36, 52, 71);
    private static final Color POR_COLOR = new Color(47, 109, 214);
    private static final Color VSH_COLOR = new Color(233, 138, 21);
    private static final Color SO_COLOR = new Color(37, 163, 93);
    private static final Color GOOD_LAYER_COLOR = new Color(55, 196, 119, 38);

    private final List<WellRecord> records;

    public VisualizationPanel(List<WellRecord> records) {
        this.records = records;
        setPreferredSize(new Dimension(960, 560));
        setBackground(BACKGROUND_COLOR);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int left = 86;
            int right = width - 56;
            int top = 84;
            int bottom = height - 78;

            g2.setColor(BACKGROUND_COLOR);
            g2.fillRect(0, 0, width, height);

            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 24));
            g2.drawString("储层参数可视化", left, 42);

            g2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
            g2.drawString("横轴是深度，纵轴是百分比。把三条结果线放在一张图里，变化趋势会更直观。", left, 64);

            g2.setColor(CHART_BACKGROUND);
            g2.fillRoundRect(left, top, right - left, bottom - top, 18, 18);

            drawGoodLayerBackground(g2, left, top, right, bottom);
            drawGrid(g2, left, top, right, bottom);
            drawSeries(g2, left, top, right, bottom, MetricType.POROSITY, POR_COLOR);
            drawSeries(g2, left, top, right, bottom, MetricType.SHALE_VOLUME, VSH_COLOR);
            drawSeries(g2, left, top, right, bottom, MetricType.OIL_SATURATION, SO_COLOR);
            drawLegend(g2, left, top);
        } finally {
            g2.dispose();
        }
    }

    private void drawGrid(Graphics2D g2, int left, int top, int right, int bottom) {
        g2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        g2.setColor(GRID_COLOR);

        for (int percent = 0; percent <= 100; percent += 20) {
            int y = percentToY(percent, top, bottom);
            g2.drawLine(left, y, right, y);
            g2.setColor(TEXT_COLOR);
            g2.drawString(percent + "%", left - 42, y + 4);
            g2.setColor(GRID_COLOR);
        }

        double minDepth = getMinDepth();
        double maxDepth = getMaxDepth();
        for (int i = 0; i <= 4; i++) {
            double rate = i / 4.0;
            int x = left + (int) ((right - left) * rate);
            g2.drawLine(x, top, x, bottom);

            double depth = minDepth + (maxDepth - minDepth) * rate;
            g2.setColor(TEXT_COLOR);
            g2.drawString(String.format(Locale.US, "%.2f", depth), x - 20, bottom + 24);
            g2.setColor(GRID_COLOR);
        }

        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 13));
        g2.drawString("深度", right - 16, bottom + 48);
        g2.drawString("参数值", left - 58, top - 18);
        g2.drawLine(left, bottom, right, bottom);
        g2.drawLine(left, top, left, bottom);
    }

    private void drawGoodLayerBackground(Graphics2D g2, int left, int top, int right, int bottom) {
        g2.setColor(GOOD_LAYER_COLOR);
        for (WellRecord record : records) {
            if (!record.isGoodOilLayer()) {
                continue;
            }

            int centerX = depthToX(record.getDepth(), left, right);
            g2.fillRoundRect(centerX - 12, top + 4, 24, bottom - top - 8, 10, 10);
        }
    }

    private void drawSeries(Graphics2D g2, int left, int top, int right, int bottom, MetricType metricType, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 0; i < records.size() - 1; i++) {
            WellRecord current = records.get(i);
            WellRecord next = records.get(i + 1);
            int x1 = depthToX(current.getDepth(), left, right);
            int y1 = percentToY(metricType.getValue(current), top, bottom);
            int x2 = depthToX(next.getDepth(), left, right);
            int y2 = percentToY(metricType.getValue(next), top, bottom);
            g2.drawLine(x1, y1, x2, y2);
        }

        for (WellRecord record : records) {
            int x = depthToX(record.getDepth(), left, right);
            int y = percentToY(metricType.getValue(record), top, bottom);
            g2.fillOval(x - 4, y - 4, 8, 8);
        }
    }

    private void drawLegend(Graphics2D g2, int left, int top) {
        int legendX = left;
        int legendY = top - 34;
        g2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));

        legendX = drawLegendItem(g2, legendX, legendY, POR_COLOR, "孔隙度");
        legendX = drawLegendItem(g2, legendX, legendY, VSH_COLOR, "泥质含量");
        legendX = drawLegendItem(g2, legendX, legendY, SO_COLOR, "含油饱和度");
        drawLegendItem(g2, legendX, legendY, GOOD_LAYER_COLOR.darker(), "好油层");
    }

    private int drawLegendItem(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillRoundRect(x, y - 10, 20, 8, 4, 4);
        g2.setColor(TEXT_COLOR);
        g2.drawString(label, x + 28, y);
        return x + 28 + g2.getFontMetrics().stringWidth(label) + 28;
    }

    private int depthToX(double depth, int left, int right) {
        double minDepth = getMinDepth();
        double maxDepth = getMaxDepth();
        double span = Math.max(0.0001, maxDepth - minDepth);
        double rate = (depth - minDepth) / span;
        return left + (int) ((right - left) * rate);
    }

    private int percentToY(double percentValue, int top, int bottom) {
        double rate = Math.max(0.0, Math.min(1.0, percentValue / 100.0));
        return bottom - (int) ((bottom - top) * rate);
    }

    private double getMinDepth() {
        double min = records.get(0).getDepth();
        for (WellRecord record : records) {
            min = Math.min(min, record.getDepth());
        }
        return min;
    }

    private double getMaxDepth() {
        double max = records.get(0).getDepth();
        for (WellRecord record : records) {
            max = Math.max(max, record.getDepth());
        }
        return max;
    }
}
