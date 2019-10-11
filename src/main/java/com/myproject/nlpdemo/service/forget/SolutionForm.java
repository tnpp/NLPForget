package com.myproject.nlpdemo.service.forget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.myproject.nlpdemo.service.forget.pojo.DictionaryObj;
import com.myproject.nlpdemo.service.forget.pojo.MemoryBondColl;
import com.myproject.nlpdemo.service.forget.pojo.MemoryItemColl;
import com.myproject.nlpdemo.utils.Utils;

public class SolutionForm {
    private final static double nmbReadSpeed = 7d; // 每秒7个字节
    static MemoryBondColl objCharBondColl = new MemoryBondColl();
    static MemoryItemColl objKeyWordColl = new MemoryItemColl();

    public static void main(String[] args) {
        SolutionForm form = new SolutionForm();
        form.catchWordIndexColl();
        System.out.println("----------------------------------------");
        List<String> segments = form.showWordSegments("led白光筒灯", 2);
        System.out.println(segments.stream().collect(Collectors.joining(", ")));
    }

    public List<DictionaryObj> catchWordIndexColl() {
        objCharBondColl = new MemoryBondColl();
        objKeyWordColl = new MemoryItemColl();

        objCharBondColl.setOffsetTotalCount(0d);
        objKeyWordColl.setOffsetTotalCount(0d);
        objCharBondColl.setMinuteOffsetSize(nmbReadSpeed * 60 * 60 * 24 * 6);
        objKeyWordColl.setMinuteOffsetSize(nmbReadSpeed * 60 * 60 * 24 * 6);

        List<String> list = Utils.readResourceFile("file/lamp_lighting_searchkey.txt");
        System.out.println("开始。。。文件中共" + list.size() + "行记录。。。");
        double dTempCharCount = objCharBondColl.getOffsetTotalCount();
        for (String line : list) {
            line = line.toLowerCase();
            line = line.replaceAll("\\p{Punct}", "").replaceAll("\\pP", "").replaceAll("　", "").replaceAll("\\p{Blank}", "").replaceAll("\\p{Space}", "")
                    .replaceAll("\\p{Cntrl}", " ").replaceAll("<.*?>", "");
            // line = line.replaceAll("\\p{C}", "").replaceAll("<.*?>", "");
            dTempCharCount += line.length();
            String text = line;
            if (text != null && text.trim().length() > 0) {
                // 当数据跑过一个周期的数据时清理一次邻键集、词库，避免内存空间不足
                if (dTempCharCount > objCharBondColl.getMinuteOffsetSize()) {
                    DictionaryDAL.clearMemoryBondColl(objCharBondColl);
                    DictionaryDAL.clearMemoryItemColl(objKeyWordColl, null);
                    dTempCharCount = 0;
                }
                WordDictService.UpdateKeyWordColl(text, objCharBondColl, objKeyWordColl);
            }
        }
        System.out.println("Generate Dictionary completely !!!");
        List<DictionaryObj> originalDics = WordDictService.ShowKeyWordWeightColl(objKeyWordColl);
        List<DictionaryObj> dics = WordDictService.handleDictionaryKeyword(originalDics);
        int i = 0;
        System.out.println("----------------------------------------");
        System.out.println(" 【主词】 | 遗忘词频 | 累计词频 | 词权值 | 成熟度(%)");
        for (DictionaryObj x : dics) {
            if (x.getWord().length() > 1) {
                System.out.println(x);
                if (++i >= 5000) {
                    break;
                }
            }
        }
        return dics;
    }

    /**
     * 
     * @param text
     *            待分词文本
     * @param maxWordLen
     *            最大词长（建议：细粒度为4、粗粒度为7）
     * @return
     */
    public List<String> showWordSegments(String text, Integer maxWordLen) {
        if (maxWordLen == null) {
            maxWordLen = 7;
        }
        if (maxWordLen == 0) {
            maxWordLen = text.length();
        }
        Map<Integer, List<String>> objKeyWordBufferDict = new HashMap<>();
        Map<Integer, Double> objKeyWordValueDict = new HashMap<Integer, Double>();
        double dLogTotalCount = Math.log(objKeyWordColl.getMinuteOffsetSize());
        char[] cs = text.trim().toLowerCase().toCharArray();
        for (int k = 0; k < cs.length; k++) {
            List<String> objKeyWordList = new ArrayList<String>();
            double dKeyWordValue = 0d;
            for (int len = 0; len <= Math.min(k, maxWordLen); len++) {
                int startpos = k - len;
                String keyword = text.substring(startpos, startpos + len + 1);
                if (len > 0 && !objKeyWordColl.contains(keyword)) {
                    continue;
                }
                double dTempValue = 0;
                if (objKeyWordColl.contains(keyword)) {
                    dTempValue = -(dLogTotalCount - Math.log(DictionaryDAL.calcRemeberValue(keyword, objKeyWordColl)));
                }
                if (objKeyWordValueDict.containsKey(startpos - 1)) {
                    dTempValue += objKeyWordValueDict.get(startpos - 1);
                    if (dKeyWordValue == 0 || dTempValue > dKeyWordValue) {
                        dKeyWordValue = dTempValue;
                        objKeyWordList = new ArrayList<>(objKeyWordBufferDict.get(startpos - 1));
                        objKeyWordList.add(keyword);
                    }
                } else {
                    if (dKeyWordValue == 0 || dTempValue > dKeyWordValue) {
                        dKeyWordValue = dTempValue;
                        objKeyWordList = new ArrayList<>();
                        objKeyWordList.add(keyword);
                    }
                }
            }
            objKeyWordBufferDict.put(k, objKeyWordList);
            objKeyWordValueDict.put(k, dKeyWordValue);
        }
        return objKeyWordBufferDict.get(cs.length - 1);
    }
}
