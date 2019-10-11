package com.myproject.nlpdemo.service.forget.pojo;

public class DictionaryObj {
    private String word; // 主词
    private double forgetFreq; // 遗忘词频
    private double freq; // 累计词频
    private double weight; // 词权重
    private double maturity; // 成熟度

    @Override
    public String toString() {
        return word + " | " + forgetFreq + " | " + freq + " | " + weight + " | " + maturity;
    }

    public DictionaryObj() {
        super();
    }

    public DictionaryObj(String word) {
        super();
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getForgetFreq() {
        return forgetFreq;
    }

    public void setForgetFreq(double forgetFreq) {
        this.forgetFreq = forgetFreq;
    }

    public double getFreq() {
        return freq;
    }

    public void setFreq(double freq) {
        this.freq = freq;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getMaturity() {
        return maturity;
    }

    public void setMaturity(double maturity) {
        this.maturity = maturity;
    }

}
