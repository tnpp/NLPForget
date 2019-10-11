package com.myproject.nlpdemo.service.lstmcrf_ner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class LstmCrfNerModelBase {
    public static enum CategoryEnum {
        LIGTH("ner_model.pb", "vocabs"), GREEN("ner_model_园林绿化.pb", "vocabs_园林绿化"), GLASSTILE("ner_model_玻璃瓷砖.pb", "vocabs_玻璃瓷砖");
        String modelFileLight = null;
        String vocabs = null;

        private CategoryEnum(String modelStr, String vocabs) {
            this.modelFileLight = modelStr;
            this.vocabs = vocabs;
        }

        public String getModelFile() {
            return this.modelFileLight;
        }

        public String getVocabs() {
            return this.vocabs;
        }
    };
    protected Map<String, Integer> readVocabMap(CategoryEnum cate) {
        String vocabPath = cate.getVocabs();
        Map<String, Integer> wordMap = new HashMap<String, Integer>();
        try (BufferedReader buffer = new BufferedReader(
                new InputStreamReader(LstmCrfNerModelBase.class.getClassLoader()
                        .getResourceAsStream("model/lstm_crf_ner/" + vocabPath
                                + "/token2id")));) {
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

    protected Map<Integer, String> readLabelMap(CategoryEnum cate) {
        String vocabPath = cate.getVocabs();
        Map<Integer, String> labelMap = new HashMap<Integer, String>();
        try (BufferedReader buffer = new BufferedReader(
                new InputStreamReader(LstmCrfNerModelBase.class.getClassLoader()
                        .getResourceAsStream("model/lstm_crf_ner/" + vocabPath
                                + "/label2id")));) {
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
