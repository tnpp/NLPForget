package com.myproject.nlpdemo.service.lstmcrf_ner;

import java.util.Map;

import org.tensorflow.Session;

public class NerModel {
    private Session session;
    private Map<String, Integer> wordVocabMap;
    private Map<Integer, String> labelMap;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Map<String, Integer> getWordVocabMap() {
        return wordVocabMap;
    }

    public void setWordVocabMap(Map<String, Integer> wordVocabMap) {
        this.wordVocabMap = wordVocabMap;
    }

    public Map<Integer, String> getLabelMap() {
        return labelMap;
    }

    public void setLabelMap(Map<Integer, String> labelMap) {
        this.labelMap = labelMap;
    }

}
