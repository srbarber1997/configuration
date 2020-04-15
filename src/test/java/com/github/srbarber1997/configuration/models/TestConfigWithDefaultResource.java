package com.github.srbarber1997.configuration.models;

import com.github.srbarber1997.configuration.ConfigurationModel;

@ConfigurationModel(defaultResource = "configResources/modelDefaultResource.config")
public class TestConfigWithDefaultResource {
    public int num = 6;
}
