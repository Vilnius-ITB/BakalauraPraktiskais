package io.github.VilniusITB.BakalauraPraktiskais;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DebugLogger {

    private static Logger logger = LoggerFactory.getLogger("VPAY DEBUG");

    /**
     * So this prevents any constructors being used on this class.
     */
    private DebugLogger() {

    }
    private static List<String> logs = new ArrayList<>();
    private static final int MAX_LOGS = 30;

    public static void clearLogs() {
        logs.clear();
    }

    public static String getLogsAsString() {
        if (logs.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String log : logs) sb.append(log).append("\n");
        return sb.toString();
    }

    public static void log(String message) {
        logs.add(message);
        logger.info(message);
        if (logs.size() > MAX_LOGS) logs.remove(0);
    }

}
