package org.bitbucket.srbarber1997.configuration.models;

import com.google.gson.GsonBuilder;
import org.bitbucket.srbarber1997.configuration.ConfigurationModel;
import org.bitbucket.srbarber1997.configuration.ConfigureSerializer;

import java.util.ArrayList;
import java.util.List;

@ConfigurationModel
public class TestConfigWithConfigure {

    @ConfigureSerializer
    public static void configure(GsonBuilder gsonBuilder) {
        gsonBuilder.setPrettyPrinting();
    }

    public int value = 70;
    public List<Integer> moreValues = new ArrayList<>();

}
