package com.myproject.nlpdemo.service.lstmcrf_ner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.tensorflow.Graph;
import org.tensorflow.Session;

public class NerModelSingleton {
    public static NerModelSingleton getInstance() {
        return SingletonHolder.instance;
    }
    private static class SingletonHolder {
        private final static NerModelSingleton instance = new NerModelSingleton();
    }

    private Map<String, NerModel> map = new HashMap<>();

    public NerModel getNerModel(String categoryName) {
        NerModel m = map.get(categoryName);
        if (m == null) {
            synchronized (Map.class) {
                m = map.get(categoryName);
                if (m == null) {
                    m = readModel(categoryName);
                    map.put(categoryName, m);
                }
            }
        }
        return m;
    }

    private NerModel readModel(String categoryName) {
        NerModel nerModel = new NerModel();
        try {
            Graph tfGraph = new Graph();
            byte[] graphBytes = IOUtils
                    .toByteArray(NerModelSingleton.class.getClassLoader()
                            .getResourceAsStream("model/lstm_crf_ner/ner_model_"
                                    + categoryName + ".pb"));
            tfGraph.importGraphDef(graphBytes);
            Session session = new Session(tfGraph);
            nerModel.setSession(session);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Integer> wordVocabMap = readVocabMap(categoryName);
        Map<Integer, String> labelMap = readLabelMap(categoryName);
        nerModel.setWordVocabMap(wordVocabMap);
        nerModel.setLabelMap(labelMap);
        return nerModel;
    }

    protected Map<String, Integer> readVocabMap(String categoryName) {
        Map<String, Integer> wordMap = new HashMap<String, Integer>();
        try (BufferedReader buffer = new BufferedReader(
                new InputStreamReader(NerModelSingleton.class.getClassLoader()
                        .getResourceAsStream("model/lstm_crf_ner/vocabs_"
                                + categoryName + "/token2id")));) {
            String line = null;
            while ((line = buffer.readLine()) != null) {
                if (StringUtils.isBlank(line.trim())) {
                    continue;
                }
                String[] arr = StringUtils.split(line, "\t");
                wordMap.put(arr[0], Integer.parseInt(arr[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wordMap;
    }

    protected Map<Integer, String> readLabelMap(String categoryName) {
        Map<Integer, String> labelMap = new HashMap<Integer, String>();
        try (BufferedReader buffer = new BufferedReader(
                new InputStreamReader(NerModelSingleton.class.getClassLoader()
                        .getResourceAsStream("model/lstm_crf_ner/vocabs_"
                                + categoryName + "/label2id")));) {
            String line = null;
            while ((line = buffer.readLine()) != null) {
                if (StringUtils.isBlank(line.trim())) {
                    continue;
                }
                String[] arr = StringUtils.split(line, "\t");
                labelMap.put(Integer.parseInt(arr[1]), arr[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return labelMap;
    }
}
