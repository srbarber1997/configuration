package com.github.srbarber1997.configuration.models;

import com.github.srbarber1997.configuration.ConfigurationModel;

@ConfigurationModel
public class TestConfig2 {
    private String stuff = "";
    private int moreStuff = 0;

    public TestConfig2() {

    }

    public TestConfig2(String stuff, int moreStuff) {
        this.stuff = stuff;
        this.moreStuff = moreStuff;
    }

    public String getStuff() {
        return stuff;
    }

    public void setStuff(String stuff) {
        this.stuff = stuff;
    }

    public int getMoreStuff() {
        return moreStuff;
    }

    public void setMoreStuff(int moreStuff) {
        this.moreStuff = moreStuff;
    }
}
