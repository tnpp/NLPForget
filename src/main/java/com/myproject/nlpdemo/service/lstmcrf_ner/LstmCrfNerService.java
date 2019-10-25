package com.myproject.nlpdemo.service.lstmcrf_ner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import com.myproject.nlpdemo.utils.TextHandler;

@Service
public class LstmCrfNerService {
    private final static Logger logger = LoggerFactory.getLogger(LstmCrfNerService.class);

    public List<String[]> getLabel(String categoryName, String text) {
        text = TextHandler.thesaurusMerge(text);
        Map<String, Integer> wordMap = NerModelSingleton.getInstance().getNerModel(categoryName).getWordVocabMap();
        Map<Integer, String> labelMap = NerModelSingleton.getInstance().getNerModel(categoryName).getLabelMap();
        int[][] txtToIds = getTextToId(text, wordMap);

        List<String[]> predictResult = new ArrayList<>();
        Session session = NerModelSingleton.getInstance().getNerModel(categoryName).getSession();
        try (Tensor inputs = Tensor.create(txtToIds);
                Tensor result = session.runner().feed("inputs_x", inputs).fetch("batch_pred_sequence").run().get(0);) {
            long[] rshape = result.shape();
            int nlabels = (int) rshape[1];
            int[][] vector = (int[][]) result.copyTo(new int[64][nlabels]);
            if (vector == null || vector.length < 1) {
                System.out.println("Cannot get result!");
            } else {
                int[] labelIds = vector[0];
                String label = "";
                String wordItem = "";
                for (int i = 0; i < text.trim().length(); i++) {
                    String charLabel = labelMap.get(labelIds[i]);
                    if (!charLabel.equalsIgnoreCase("O")) {
                        if (charLabel.startsWith("B")) {
                            if (StringUtils.isNotBlank(label) && StringUtils.isNotBlank(wordItem)) {
                                predictResult.add(new String[] { label, wordItem });
                                label = wordItem = "";
                            }
                            label = charLabel.substring(2);
                            wordItem = "" + text.charAt(i);
                        } else {
                            wordItem += text.charAt(i);
                        }
                    } else {
                        if (StringUtils.isNotBlank(label) && StringUtils.isNotBlank(wordItem)) {
                            predictResult.add(new String[] { label, wordItem });
                            label = wordItem = "";
                        }
                    }
                }
                if (StringUtils.isNotBlank(label) && StringUtils.isNotBlank(wordItem)) {
                    predictResult.add(new String[] { label, wordItem });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictResult;
    }

    private static void print(List<String[]> ls) {
        if (CollectionUtils.isNotEmpty(ls)) {
            for (String[] l : ls) {
                System.out.println(l[0] + ":" + l[1]);
            }
        } else {
            System.out.println("No label !!!");
        }
    }

    public static void main(String[] args) {
        LstmCrfNerService s = new LstmCrfNerService();
        print(s.getLabel("灯具、光源", "led灯"));
        print(s.getLabel("灯具、光源", "三管荧光灯"));
        print(s.getLabel("灯具、光源", "led节能通道(25w)"));
        print(s.getLabel("园林绿化", "罗汉松造型树"));
        print(s.getLabel("园林绿化", "山里红树"));
        print(s.getLabel("园林绿化", "led节能通道(25w)"));
        print(s.getLabel("园林绿化", "绿羽毛"));
        print(s.getLabel("玻璃、陶瓷及面砖", "600*1200*10玻化砖"));
        print(s.getLabel("玻璃、陶瓷及面砖", "(伽马灰)夹胶玻璃"));
    }
    private int[][] getTextToId(String text, Map<String, Integer> word_to_id) {
        int[][] txtToIds = new int[64][300];
        char[] chs = text.trim().toLowerCase().toCharArray();
        List<Integer> list = new ArrayList<Integer>();
        for (char c : chs) {
            String element = Character.toString(c);
            if (word_to_id.containsKey(element)) {
                list.add(word_to_id.get(element));
            } else {
                list.add(word_to_id.get("<UNK>"));
                logger.error("WORD: [" + element + "] does not in Vocab !!!");
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return txtToIds;
        }
        int size = list.size();
        Integer[] targetInter = (Integer[]) list.toArray(new Integer[size]);
        int[] target = Arrays.stream(targetInter).mapToInt(Integer::valueOf).toArray();
        if (size <= 300) {
            System.arraycopy(target, 0, txtToIds[0], 0, target.length);
        } else {
            System.arraycopy(target, 0, txtToIds[0], 0, 300);
            logger.warn("Words length is over 300 !!!");
        }
        return txtToIds;
    }

}
