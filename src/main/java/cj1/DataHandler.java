package cj1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataHandler implements IDataProcessor {

    private static final int HEADER_LINES = 7;
    private static final int DATA_START_LINE = 39;
    private static final int DEPTH_POINTS = 15;
    private static final int RAW_COLUMN_COUNT = 10;

    private static final double GCUR = 3.7000;
    private static final double GR_MAX = 156.0010;
    private static final double GR_MIN = 52.2120;
    private static final double DT_FLUID = 189.0000;
    private static final double DT_MATRIX = 88.0;
    private static final double RW = 0.0400;
    private static final double A = 0.6600;
    private static final double B = 1.0000;
    private static final double M = 1.6496;
    private static final double N = 2.0000;

    private final Path parameterFile;
    private final Path logFile;
    private final List<String> headerInfo = new ArrayList<>();
    private final List<String> parameters = new ArrayList<>();
    private final List<WellRecord> records = new ArrayList<>();

    public DataHandler(Path dataDirectory) throws IOException {
        this.parameterFile = dataDirectory.resolve("parameters.txt");
        this.logFile = dataDirectory.resolve("well_logging_data.txt");
        loadData();
        processWellData();
    }

    @Override
    public void loadData() throws IOException {
        headerInfo.clear();
        parameters.clear();
        records.clear();

        try (BufferedReader logReader = Files.newBufferedReader(logFile, StandardCharsets.UTF_8);
             BufferedReader parameterReader = Files.newBufferedReader(parameterFile, StandardCharsets.UTF_8)) {

            for (int i = 0; i < HEADER_LINES; i++) {
                String line = logReader.readLine();
                if (line == null) {
                    throw new IOException("测井文件头部不完整，至少需要 " + HEADER_LINES + " 行。");
                }
                headerInfo.add(line);
            }

            for (int i = 0; i < DATA_START_LINE - HEADER_LINES - 1; i++) {
                if (logReader.readLine() == null) {
                    throw new IOException("还没走到数据区，源文件就已经结束了。");
                }
            }

            for (int i = 0; i < DEPTH_POINTS; i++) {
                String line = logReader.readLine();
                if (line == null) {
                    break;
                }
                if (!line.trim().isEmpty()) {
                    records.add(parseRecord(line));
                }
            }

            String line;
            while ((line = parameterReader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    parameters.add(line);
                }
            }
        }

        if (records.isEmpty()) {
            throw new IOException("没有读到任何测井数据，请检查数据文件和起始行设置。");
        }
    }

    private WellRecord parseRecord(String line) throws IOException {
        String[] items = line.trim().split("\\s+");
        if (items.length < RAW_COLUMN_COUNT) {
            throw new IOException("数据列数不够，原始行内容为：" + line);
        }

        double[] values = new double[RAW_COLUMN_COUNT];
        try {
            for (int i = 0; i < RAW_COLUMN_COUNT; i++) {
                values[i] = Double.parseDouble(items[i]);
            }
        } catch (NumberFormatException e) {
            throw new IOException("数据里混入了无法解析的数值，原始行内容为：" + line, e);
        }

        return new WellRecord(line, values);
    }

    @Override
    public void processWellData() {
        for (WellRecord record : records) {
            double porosity = (record.getDt() - DT_MATRIX) / (DT_FLUID - DT_MATRIX);
            porosity = clamp(porosity, 0.005, 0.4);

            double shaleIndex = (record.getGr() - GR_MIN) / (GR_MAX - GR_MIN);
            double shaleVolume = (Math.pow(2, GCUR * shaleIndex) - 1) / (Math.pow(2, GCUR) - 1);
            shaleVolume = clamp(shaleVolume, 0.0, 1.0);

            double safeResistivity = Math.max(record.getLld(), 0.0001);
            double waterSaturation = Math.pow((A * B * RW) / (Math.pow(porosity, M) * safeResistivity), 1.0 / N);
            double oilSaturation = clamp(1 - waterSaturation, 0.0, 1.0);

            record.setPorosity(porosity);
            record.setShaleVolume(shaleVolume);
            record.setOilSaturation(oilSaturation);
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public List<WellRecord> getRecords() {
        return records;
    }

    @Override
    public List<String> getHeaderInfo() {
        return headerInfo;
    }

    @Override
    public List<String> getParameters() {
        return parameters;
    }
}
