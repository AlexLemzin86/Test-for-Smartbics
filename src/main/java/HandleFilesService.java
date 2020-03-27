import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class HandleFilesService {
    private static final Logger LOGGER = Logger.getLogger(HandleFilesService.class);
    private static final int VALUE_0 = 0;
    private static final int VALUE_1 = 1;
    private static final int VALUE_9 = 9;
    private static final long LONG_VALUE_1 = 1L;
    private static final String REGEX_FOR_ERRORMAP_KEY = "%s, %s.00-%s.00";
    private static final String REGEX_FOR_WRITER = "%s Колличество ошибок: %d%n";
    private static final String REGEX_FOR_STREAMS_LINE = ";";

    void countErrorsFromFiles(String... filePaths) {
        Map<String, Integer> errorsMap = new HashMap<>();
        for (String filePath : filePaths) {
            Path path = Paths.get(filePath);

            try (Stream<String> streamFile = Files.lines(path)) {
                streamFile
                        .filter(line -> !line.isEmpty())
                        .map(line -> Arrays.asList(line.split(REGEX_FOR_STREAMS_LINE)))
                        .filter(stringList -> "ERROR".equalsIgnoreCase(stringList.get(VALUE_1)))
                        .forEach(stringList -> {
                            LocalDateTime errorDateTime = LocalDateTime.parse(stringList.get(VALUE_0),
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            final int timePlusHours = errorDateTime.plusHours(LONG_VALUE_1).getHour();
                            String keyPattern = String.format(REGEX_FOR_ERRORMAP_KEY, errorDateTime.toLocalDate(),
                                    checkCountSymbols(errorDateTime.getHour()),
                                    checkCountSymbols(timePlusHours));
                            errorsMap.put(keyPattern, errorsMap.getOrDefault(keyPattern, VALUE_0) + VALUE_1);
                        });
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }

        writeToFile(errorsMap);
    }

    private Object checkCountSymbols(int hour) {
        return hour > VALUE_9 ? hour : "0" + hour;
    }

    private void writeToFile(Map<String, Integer> errorsMap) {
        String outFilePath = "src/main/resources/statistics.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFilePath, true))) {
            errorsMap.entrySet()
                    .stream()
                    .sorted((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()))
                    .forEach(stringIntegerEntry -> {
                        try {
                            writer.write(String.format(REGEX_FOR_WRITER,
                                    stringIntegerEntry.getKey(), stringIntegerEntry.getValue()));
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage());
                        }
                    });
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
