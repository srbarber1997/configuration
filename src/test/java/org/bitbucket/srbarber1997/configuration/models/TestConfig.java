package org.bitbucket.srbarber1997.configuration.models;

import org.bitbucket.srbarber1997.configuration.ConfigurationModel;

@ConfigurationModel
public class TestConfig {
    private String word = "";
    private int num = 0;

    public TestConfig() {

    }

    public TestConfig(String word, int num) {
        this.word = word;
        this.num = num;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
