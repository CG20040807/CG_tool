package cj1;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;

public class ChartExporter {

    private ChartExporter() {
    }

    public static void export(List<WellRecord> records, Path imagePath) throws IOException {
        Files.createDirectories(imagePath.getParent());

        VisualizationPanel panel = new VisualizationPanel(records);
        panel.setSize(panel.getPreferredSize());

        BufferedImage image = new BufferedImage(
            panel.getPreferredSize().width,
            panel.getPreferredSize().height,
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = image.createGraphics();
        panel.paint(g2);
        g2.dispose();

        ImageIO.write(image, "png", imagePath.toFile());
    }
}
