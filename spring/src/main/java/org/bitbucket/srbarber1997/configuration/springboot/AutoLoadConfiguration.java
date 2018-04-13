package org.bitbucket.srbarber1997.configuration.springboot;

import org.bitbucket.srbarber1997.configuration.ConfigLoader;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * Class that will be used as Configuration for a
 * Spring Boot Application. Uses the {@link ConfigLoader}
 * to load configs when the application starts.
 * @author srbarber1997
 */
@org.springframework.context.annotation.Configuration
public class AutoLoadConfiguration {

    @Value("${configuration.auto-load:true}")
    private String autoLoad;

    @Value("${configuration.log-output:true}")
    private String logOutput;

    /**
     * Method that will be run after spring
     * auto-wiring has finished. Uses application
     * properties to determine whether to auto load
     * and/or log output.
     */
    @PostConstruct
    public void loadConfigAtApplicationLaunch() {
        if (autoLoad.equals("true"))
            ConfigLoader.load(logOutput.equals("true"));
    }
}
