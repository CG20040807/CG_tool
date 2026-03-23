package cj1;

import java.io.IOException;
import java.util.List;

public interface IDataProcessor {
    void loadData() throws IOException;

    void processWellData();

    List<WellRecord> getRecords();

    List<String> getHeaderInfo();

    List<String> getParameters();
}
