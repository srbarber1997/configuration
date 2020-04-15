package com.github.srbarber1997.configuration.models;

import com.github.srbarber1997.configuration.ConfigurationModel;
import com.github.srbarber1997.configuration.Description;

@ConfigurationModel
public class TestConfigsWithDescription {

    public static final String description = "The string used to greet you as you load the app";

    @Description(description)
    public String greeting = "Hello, ";
}
