package cj1;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Renwu4 {

    private static final Path DEFAULT_DATA_DIRECTORY = Paths.get("data");
    private static final Path DEFAULT_OUTPUT_DIRECTORY = Paths.get("output");

    public static void main(String[] args) {
        System.out.println("===== 测井数据处理系统 =====");

        Path dataDirectory = args.length > 0 ? Paths.get(args[0]) : DEFAULT_DATA_DIRECTORY;
        Path outputDirectory = args.length > 1 ? Paths.get(args[1]) : DEFAULT_OUTPUT_DIRECTORY;

        try {
            DataHandler dataHandler = new DataHandler(dataDirectory);
            SystemFunctions systemFunctions = new SystemFunctions(dataHandler, outputDirectory, System.out);
            systemFunctions.startSystem();
        } catch (IOException e) {
            System.err.println("程序启动失败：" + e.getMessage());
        }
    }
}
