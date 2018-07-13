package org.bitbucket.srbarber1997.configuration.models;

import org.bitbucket.srbarber1997.configuration.ConfigurationModel;

@ConfigurationModel(initOnReload = false)
public class TestConfigWithInitMethodOnReload {
    private static Throwable throwable = null;

    public void init() throws Throwable {
        if (throwable != null)
            throw throwable;
    }

    public static void setThrowable(Throwable throwable) {
        TestConfigWithInitMethodOnReload.throwable = throwable;
    }
}
