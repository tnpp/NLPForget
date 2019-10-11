package com.myproject.nlpdemo.service.forget;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myproject.nlpdemo.service.forget.pojo.DictionaryObj;
import com.myproject.nlpdemo.service.forget.pojo.MemoryBondColl;
import com.myproject.nlpdemo.service.forget.pojo.MemoryItemColl;
import com.myproject.nlpdemo.service.forget.pojo.MemoryMDL;
import com.myproject.nlpdemo.utils.Utils;

public class WordDictService {
    /**
     * 从文本中生成候选词
     * 
     * @param text 文本行
     * @param objCharBondColl 相邻字典
     * @param objKeyWordColl 词库
     */
    public static void UpdateKeyWordColl(String text, MemoryBondColl objCharBondColl, MemoryItemColl objKeyWordColl) {
        boolean bUpdateCharBondColl = true; // 是否更新相邻字典
        boolean bUpdateKeyWordColl = true; // 是否更新词库
        if (text == null || text.trim().length() <= 0) {
            return;
        }
        text = text.trim();
        char[] cs = text.toCharArray();
        StringBuilder buffer = new StringBuilder(); // 用于存放连续的子串
        String keyHead = "" + cs[0]; // keyHead、keyTail分别存放相邻的两个字符
        buffer.append(keyHead);
        for (int k = 1; k < cs.length; k++) { // 遍历句子中的每一个字符
            //从句子中取一个字作为相邻两字的尾字
            String keyTail = "" + cs[k];
            if (bUpdateCharBondColl)
            {
                //更新相邻字典
                DictionaryDAL.updateMemoryBondColl(keyHead, keyTail, objCharBondColl);
            }
            if (bUpdateKeyWordColl) {
                // 判断相邻两字是否有关
                if (!DictionaryDAL.isBondValid(keyHead, keyTail, objCharBondColl)) {
                    // 两字无关，则将绥中的字串取出，此即为候选词
                    String keyword = buffer.toString();
                    // 将候选词添加到词库中
                    DictionaryDAL.UpdateMemoryItemColl(keyword, objKeyWordColl);
                    buffer = new StringBuilder(); // 清空缓冲
                    buffer.append(keyTail); // 并开始下一个子串
                } else {
                    buffer.append(keyTail); // 两个字有关，则将当前字追加至串缓冲中
                }
            }
            // 将当前的字作为相邻的首字
            keyHead = keyTail;
        }
    }
    
    /**
     * 按权重排序输出词库
     * 
     * @param objMemoryItemColl 词库
     * @param nKeyWordTopCount 输出词的数量
     * @return 输出的结果
     */
    public static List<DictionaryObj> ShowKeyWordWeightColl(MemoryItemColl objMemoryItemColl) {
        double sum = 0;
        for (Map.Entry<String, MemoryMDL> s : objMemoryItemColl.memoryItemMDL.entrySet()) {
            MemoryMDL mdl = s.getValue();
            sum += (mdl.getValidDegree() / mdl.getValidCount());
        }
        double dTotalVaildDegree = sum / objMemoryItemColl.memoryItemMDL.size();
        System.out.println("词库成熟度：" + (dTotalVaildDegree > 1 ? 0 : ((1 - dTotalVaildDegree) * 100)));
        List<DictionaryObj> dicList = new ArrayList<>();
        for (Map.Entry<String, MemoryMDL> x : objMemoryItemColl.memoryItemMDL.entrySet()) {
            MemoryMDL mdl = x.getValue();
            DictionaryObj obj = new DictionaryObj(x.getKey());
            double forgetFreq = DictionaryDAL.calcRemeberValue(x.getKey(), objMemoryItemColl);
            double totalCount = mdl.getTotalCount();
            double weight = (mdl.getValidCount() <= 0 ? 0 : (mdl.getValidCount()) * (Math.log(objMemoryItemColl.getMinuteOffsetSize()) - Math.log(mdl.getValidCount())));
            double maturity = mdl.getValidCount() <= 1 ? 0
                    : mdl.getValidDegree() / mdl.getValidCount() > 1 ? 0 : Utils.round2((1 - mdl.getValidDegree() / mdl.getValidCount()) * 100);
            obj.setForgetFreq(forgetFreq);
            obj.setFreq(totalCount);
            obj.setWeight(weight);
            obj.setMaturity(maturity);
            dicList.add(obj);
        }
        dicList.sort((a, b) -> ((b.getForgetFreq() - a.getForgetFreq()) > 0d ? 1 : -1));
        return dicList;
    }

    public static List<DictionaryObj> handleDictionaryKeyword(List<DictionaryObj> list) {
        List<DictionaryObj> result = new ArrayList<DictionaryObj>();
        Set<String> words = new LinkedHashSet<String> ();
        for (DictionaryObj x: list) {
            String word = x.getWord();
            if (!Utils.isDigit(word) && word.length()>1) {
                if (Utils.isFineGrained(words, word)) {
                    result.add(x);
                    words.add(word);
                }
            }
        }
        return result;
    }
}
