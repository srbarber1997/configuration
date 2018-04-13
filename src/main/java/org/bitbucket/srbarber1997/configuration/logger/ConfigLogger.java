package org.bitbucket.srbarber1997.configuration.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfigLogger {

    private static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss:SSS");

    public void log(String message) {
        System.out.println(
                "[" + LocalDateTime.now().format(formatter) + "]"
                + " [Config Loader] " + message);
    }

    public void error(Exception e) {
        e.printStackTrace();
    }

    public static void setFormatter(DateTimeFormatter formatter) {
        ConfigLogger.formatter = formatter;
    }
}
