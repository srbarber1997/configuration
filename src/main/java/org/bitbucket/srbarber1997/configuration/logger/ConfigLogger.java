package org.bitbucket.srbarber1997.configuration.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfigLogger {

    private static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss:SSS");

    private boolean log = true;

    public void log(String message) {
        if (log) System.out.println(
                "[" + LocalDateTime.now().format(formatter) + "]"
                + " [Config Loader] " + message);
    }

    public void error(Exception e) {
        if (log)
            e.printStackTrace();
    }

    public static void setFormatter(DateTimeFormatter formatter) {
        ConfigLogger.formatter = formatter;
    }

    public void setLog(boolean log) {
        this.log = log;
    }
}
