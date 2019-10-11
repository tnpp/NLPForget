package com.myproject.nlpdemo.service.lstmcrf_ner;

import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.tensorflow.Graph;
import org.tensorflow.Session;

public class LstmCrfNerModel extends LstmCrfNerModelBase {
    private Session session;
    private Map<String, Integer> wordVocabMap;
    private Map<Integer, String> labelMap;

    private LstmCrfNerModel(CategoryEnum cate) {
        try {
            Graph tfGraph = new Graph();
            byte[] graphBytes = IOUtils.toByteArray(LstmCrfNerModel.class.getClassLoader().getResourceAsStream("model/lstm_crf_ner/" + cate.getModelFile()));
            tfGraph.importGraphDef(graphBytes);
            this.session = new Session(tfGraph);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.wordVocabMap = readVocabMap(cate);
        this.labelMap = readLabelMap(cate);
    }

    public Session getTfSession() {
        return this.session;
    }

    public Map<String, Integer> getWordVocabMap() {
        return this.wordVocabMap;
    }

    public Map<Integer, String> getLabelMap() {
        return this.labelMap;
    }

    /*
     * 生成灯具光源模型
     */
    private static class SingletonHolderForLight {
        private final static LstmCrfNerModel instance = new LstmCrfNerModel(CategoryEnum.LIGTH);
    }
    /*
     * 生成园林绿化模型
     */
    private static class SingletonHolderForGreen {
        private final static LstmCrfNerModel instance = new LstmCrfNerModel(CategoryEnum.GREEN);
    }

    /*
     * 生成玻璃瓷砖模型
     */
    private static class SingletonHolderForGlassTile {
        private final static LstmCrfNerModel instance = new LstmCrfNerModel(CategoryEnum.GLASSTILE);
    }

    public static LstmCrfNerModel getInstance(CategoryEnum cate) {
        if (cate.equals(CategoryEnum.LIGTH)) {
            return SingletonHolderForLight.instance;
        } else if (cate.equals(CategoryEnum.GREEN)) {
            return SingletonHolderForGreen.instance;
        } else if (cate.equals(CategoryEnum.GLASSTILE)) {
            return SingletonHolderForGlassTile.instance;
        } else {
            return null;
        }
    }

}
