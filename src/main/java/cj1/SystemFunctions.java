package cj1;

import java.awt.GraphicsEnvironment;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import javax.swing.SwingUtilities;

public class SystemFunctions implements ISystemFunctions {

    private static final String TABLE_HEADER =
        "#DEPTH    CAL      DT        GR        LLD      LLS      MSFL    NPHI    RHOB    SP    POR(%)    VSH(%)    So(%)";

    private final DataHandler dataHandler;
    private final Path outputDirectory;
    private final PrintStream out;

    public SystemFunctions(DataHandler dataHandler, Path outputDirectory, PrintStream out) throws IOException {
        this.dataHandler = dataHandler;
        this.outputDirectory = outputDirectory;
        this.out = out;
        Files.createDirectories(outputDirectory);
    }

    @Override
    public void displayExtractedData() {
        Path reportFile = outputDirectory.resolve("数据提取与检查.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(reportFile, StandardCharsets.UTF_8)) {
            out.println("原始测井数据提取：");
            for (String headerLine : dataHandler.getHeaderInfo()) {
                out.println(headerLine);
                writer.write(headerLine);
                writer.newLine();
            }

            for (WellRecord record : dataHandler.getRecords()) {
                String rawText = record.formatRawData();
                out.println(rawText);
                writer.write(rawText);
                writer.newLine();
            }

            out.println("测井数据检验：");
            writer.write("测井数据检验：");
            writer.newLine();

            for (WellRecord record : dataHandler.getRecords()) {
                String verifyText = record.formatVerificationData();
                out.println(verifyText);
                writer.write(verifyText);
                writer.newLine();
            }

            out.println("参数数据：");
            writer.write("参数数据：");
            writer.newLine();

            for (String parameter : dataHandler.getParameters()) {
                out.println(parameter);
                writer.write(parameter);
                writer.newLine();
            }

            out.println("检查结果已经写入：" + reportFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("输出检查结果时出错：" + e.getMessage());
        }
    }

    @Override
    public void processAndCleanData() {
        Path resultFile = outputDirectory.resolve("大数据22304班_Results_37.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(resultFile, StandardCharsets.UTF_8)) {
            writer.write(TABLE_HEADER);
            writer.newLine();

            for (WellRecord record : dataHandler.getRecords()) {
                writer.write(record.formatFullResult());
                writer.newLine();
            }

            out.println("数据处理完成，结果已保存到：" + resultFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("写入处理结果时出错：" + e.getMessage());
        }
    }

    @Override
    public void queryDataByIndex(int index) {
        int validIndex = index - 1;
        if (validIndex < 0 || validIndex >= dataHandler.getRecords().size()) {
            out.println("错误：序号超出范围，请输入 1 到 " + dataHandler.getRecords().size() + " 之间的数字。");
            return;
        }

        out.println(dataHandler.getRecords().get(validIndex).formatFullResult());
    }

    @Override
    public void calculateStatistics() {
        out.println(buildStatisticsText());
    }

    @Override
    public void sortByOilSaturation() {
        List<WellRecord> sortedRecords = new ArrayList<>(dataHandler.getRecords());
        sortedRecords.sort(Comparator.comparingDouble(WellRecord::getOilSaturationPercent).reversed());

        out.println(TABLE_HEADER);
        for (WellRecord record : sortedRecords) {
            out.println(record.formatFullResult());
        }
    }

    @Override
    public void classifyReservoirLayers() {
        List<WellRecord> class1 = new ArrayList<>();
        List<WellRecord> class2 = new ArrayList<>();
        List<WellRecord> class3 = new ArrayList<>();
        List<WellRecord> class4 = new ArrayList<>();

        for (WellRecord record : dataHandler.getRecords()) {
            if (record.getShaleVolume() > 0.25) {
                continue;
            }

            if (record.getPorosity() > 0.12) {
                class1.add(record);
            } else if (record.getPorosity() > 0.08) {
                class2.add(record);
            } else if (record.getPorosity() >= 0.05) {
                class3.add(record);
            } else {
                class4.add(record);
            }
        }

        printReservoirGroup("I类储层", class1);
        printReservoirGroup("II类储层", class2);
        printReservoirGroup("III类储层", class3);
        printReservoirGroup("IV类储层", class4);
    }

    @Override
    public void findGoodOilLayers() {
        List<WellRecord> goodOilLayers = new ArrayList<>();
        for (WellRecord record : dataHandler.getRecords()) {
            if (record.isGoodOilLayer()) {
                goodOilLayers.add(record);
            }
        }

        out.printf("好油层数量: %d%n", goodOilLayers.size());
        if (goodOilLayers.isEmpty()) {
            return;
        }

        out.println(TABLE_HEADER);
        for (WellRecord record : goodOilLayers) {
            out.println(record.formatFullResult());
        }
    }

    @Override
    public void visualizeResults() {
        Path imagePath = outputDirectory.resolve("well-logging-visualization.png");

        try {
            ChartExporter.export(dataHandler.getRecords(), imagePath);
            out.println("可视化图片已导出到：" + imagePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("导出可视化图片失败：" + e.getMessage());
        }

        if (GraphicsEnvironment.isHeadless()) {
            out.println("当前环境无法弹出图形窗口，但 PNG 已经生成。");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            VisualizationFrame frame = new VisualizationFrame(dataHandler.getRecords(), buildStatisticsText(), imagePath);
            frame.setVisible(true);
        });
    }

    public void startSystem() {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
            while (true) {
                printMenu();
                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1":
                        displayExtractedData();
                        break;
                    case "2":
                        processAndCleanData();
                        break;
                    case "3":
                        out.print("请输入数据所在序号（1~15）：");
                        handleSingleRecordQuery(scanner);
                        break;
                    case "4":
                        calculateStatistics();
                        break;
                    case "5":
                        sortByOilSaturation();
                        break;
                    case "6":
                        classifyReservoirLayers();
                        break;
                    case "7":
                        findGoodOilLayers();
                        break;
                    case "8":
                        visualizeResults();
                        break;
                    case "9":
                        out.println("感谢使用测井数据处理系统，再见！");
                        return;
                    default:
                        out.println("无效的功能号，请重新输入。");
                }

                out.println();
            }
        }
    }

    private void printReservoirGroup(String groupName, List<WellRecord> records) {
        out.printf("%s数量: %d%n", groupName, records.size());
        if (records.isEmpty()) {
            return;
        }

        out.println(TABLE_HEADER);
        for (WellRecord record : records) {
            out.println(record.formatFullResult());
        }
    }

    private void handleSingleRecordQuery(Scanner scanner) {
        String input = scanner.nextLine().trim();
        try {
            int index = Integer.parseInt(input);
            out.println(TABLE_HEADER);
            queryDataByIndex(index);
        } catch (NumberFormatException e) {
            out.println("输入无效，请输入数字。");
        }
    }

    private void printMenu() {
        out.println("测井数据处理系统菜单");
        out.println("1. 数据提取与检查");
        out.println("2. 测井数据处理与数据清洗");
        out.println("3. 深度点序号查询处理成果数据条");
        out.println("4. 统计储层参数最大值、最小值、平均值");
        out.println("5. 按含油饱和度排序");
        out.println("6. 统计不同等级储层深度点数目");
        out.println("7. 统计查询好油层深度点成果条");
        out.println("8. 结果可视化");
        out.println("9. 退出系统");
        out.print("请输入功能号: ");
    }

    private String buildStatisticsText() {
        StringBuilder builder = new StringBuilder();
        builder.append(buildStatisticLine(MetricType.POROSITY)).append(System.lineSeparator());
        builder.append(buildStatisticLine(MetricType.SHALE_VOLUME)).append(System.lineSeparator());
        builder.append(buildStatisticLine(MetricType.OIL_SATURATION)).append(System.lineSeparator());
        builder.append("好油层数量: ").append(countGoodOilLayers()).append(System.lineSeparator());
        builder.append(String.format(Locale.US, "深度范围: %.2f - %.2f", getMinDepth(), getMaxDepth()));
        return builder.toString();
    }

    private String buildStatisticLine(MetricType metricType) {
        double max = metricType.getValue(dataHandler.getRecords().get(0));
        double min = max;
        double sum = 0.0;

        for (WellRecord record : dataHandler.getRecords()) {
            double value = metricType.getValue(record);
            max = Math.max(max, value);
            min = Math.min(min, value);
            sum += value;
        }

        double average = sum / dataHandler.getRecords().size();
        return String.format(Locale.US, "%s: max=%.2f%%  min=%.2f%%  avg=%.2f%%", metricType.getLabel(), max, min, average);
    }

    private int countGoodOilLayers() {
        int count = 0;
        for (WellRecord record : dataHandler.getRecords()) {
            if (record.isGoodOilLayer()) {
                count++;
            }
        }
        return count;
    }

    private double getMinDepth() {
        double min = dataHandler.getRecords().get(0).getDepth();
        for (WellRecord record : dataHandler.getRecords()) {
            min = Math.min(min, record.getDepth());
        }
        return min;
    }

    private double getMaxDepth() {
        double max = dataHandler.getRecords().get(0).getDepth();
        for (WellRecord record : dataHandler.getRecords()) {
            max = Math.max(max, record.getDepth());
        }
        return max;
    }
}
