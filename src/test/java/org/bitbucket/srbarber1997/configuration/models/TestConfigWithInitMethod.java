package org.bitbucket.srbarber1997.configuration.models;

import org.bitbucket.srbarber1997.configuration.ConfigurationModel;

@ConfigurationModel
public class TestConfigWithInitMethod {
    private int number = 9;
    private static Throwable throwable = null;

    public TestConfigWithInitMethod() {
        number = 9;
    }

    public void init() throws Throwable {
        number += 1;

        if (throwable != null)
            throw throwable;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public static void setThrowable(Throwable throwable) {
        TestConfigWithInitMethod.throwable = throwable;
    }
}
