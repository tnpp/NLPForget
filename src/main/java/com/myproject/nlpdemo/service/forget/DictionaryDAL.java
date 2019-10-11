package com.myproject.nlpdemo.service.forget;

import java.util.Iterator;
import java.util.Map.Entry;

import com.myproject.nlpdemo.service.forget.pojo.MemoryBondColl;
import com.myproject.nlpdemo.service.forget.pojo.MemoryBondMDL;
import com.myproject.nlpdemo.service.forget.pojo.MemoryItemColl;
import com.myproject.nlpdemo.service.forget.pojo.MemoryMDL;

public class DictionaryDAL {

    /**
     * 清理邻键集合，移除低于阈值的邻键
     */
    public static void clearMemoryBondColl(MemoryBondColl objMemoryBondColl) {
        double dMinValidValue = 1.28;
        Iterator<Entry<String, MemoryBondMDL>> itr = objMemoryBondColl.memoryBondMDL.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, MemoryBondMDL> entry = itr.next();
            String key = entry.getKey();
            if (entry.getValue().getKeyItem().getValidCount() < dMinValidValue) {
                itr.remove();
            } else {
                clearMemoryItemColl(objMemoryBondColl.get(key).getLinkColl(), dMinValidValue);
            }
        }

    }

    public static void clearMemoryItemColl(MemoryItemColl objMemoryItemColl, Double dMinValidValue) {
        if (dMinValidValue == null) {
            dMinValidValue = 1.25;
        }
        Iterator<Entry<String, MemoryMDL>> itr = objMemoryItemColl.memoryItemMDL.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, MemoryMDL> entry = itr.next();
            // 此处使用最后一次的计数即可，无需计算当前剩余值
            // double dValidValue = calcRemeberValue(entry.getKey(), objMemoryItemColl);
            // if (dValidValue < dMinValidValue) itr.remove();
            if (entry.getValue().getValidCount() < dMinValidValue) {
                itr.remove();
            }
        }

    }

    public static void updateMemoryBondColl(String keyHead, String keyTail, MemoryBondColl objMemoryBondColl) {
        if (!objMemoryBondColl.contains(keyHead)) {
            MemoryBondMDL bond = new MemoryBondMDL();
            bond.getLinkColl().setOffsetTotalCount(0);
            bond.getLinkColl().setMinuteOffsetSize(objMemoryBondColl.getMinuteOffsetSize());
            objMemoryBondColl.add(keyHead, bond);
        }
        MemoryBondMDL objBondMDL = objMemoryBondColl.get(keyHead);

        MemoryMDL mdl = objBondMDL.getKeyItem();
        double dRemeberValue = MemoryDAL.calcRemeberValue(objMemoryBondColl.getOffsetTotalCount() - mdl.getUpdateOffsetCount(), objMemoryBondColl.getMinuteOffsetSize());
        mdl.setTotalCount(mdl.getTotalCount() + 1); // 累加总计数
        mdl.setValidDegree(calcValidDegree(mdl, dRemeberValue)); // 计算成熟度
        mdl.setValidCount(mdl.getValidCount() * dRemeberValue + 1); // 遗忘累频=记忆保留量+1
        mdl.setUpdateOffsetCount(objMemoryBondColl.getOffsetTotalCount()); // 更新时的偏移量

        MemoryItemColl objLinkColl = objBondMDL.getLinkColl();
        objLinkColl.setOffsetTotalCount(objMemoryBondColl.getOffsetTotalCount()); // 更新时的偏移量
        UpdateMemoryItemColl(keyTail, objLinkColl);

        objMemoryBondColl.setOffsetTotalCount(objMemoryBondColl.getOffsetTotalCount() + 1);
    }

    /**
     * 将候选项添加到词典中
     * 
     * @param keyItem 候选项
     * @param objMemoryItemColl 候选项词典
     */
    public static void UpdateMemoryItemColl(String keyItem, MemoryItemColl objMemoryItemColl) {
        if (!objMemoryItemColl.contains(keyItem)) { // 如果词典中不存在该候选项
            // 声明数据对象，用于存放候选项及其相关数据
            MemoryMDL mdl = new MemoryMDL();
            mdl.setTotalCount(1); // 候选项出现的物理次数
            mdl.setValidCount(1); // 边遗忘边累加共同作用下的有效次数
            mdl.setValidDegree(1); // 该词的默认成熟度
            objMemoryItemColl.add(keyItem, mdl); // 添加至词典中
        } else { // 如果词典中已包含该候选项
            // 从词典中取出该候选项
            MemoryMDL mdl = objMemoryItemColl.get(keyItem);
            // 计算从最后一次入库至现在这段时间剩余量系数
            double dRememberValue = MemoryDAL.calcRemeberValue(objMemoryItemColl.getOffsetTotalCount() - mdl.getUpdateOffsetCount(), objMemoryItemColl.getMinuteOffsetSize());
            mdl.setTotalCount(mdl.getTotalCount() + 1); // 累加总计数
            mdl.setValidDegree(calcValidDegree(mdl, dRememberValue)); // 计算成熟度
            mdl.setValidCount(mdl.getValidCount() * dRememberValue + 1); // 遗忘累频=记忆保留量+1
            mdl.setUpdateOffsetCount(objMemoryItemColl.getOffsetTotalCount()); // 更新时的偏移量（相当于记录本次入库的时间）
        }
        objMemoryItemColl.setOffsetTotalCount(objMemoryItemColl.getOffsetTotalCount() + 1); // 处理过的数据总量（相当于一个全局的计时器）
    }

    /**
     * 计算当前关键词的成熟度 <br>
     * 1、成熟度这里用对象遗忘与增加的量的残差累和来表征； <br>
     * 2、已经累计的残差之和会随时间衰减； <br>
     * 3、公式的意思是： 成熟度 = 成熟度衰减剩余量 + 本次遗忘与增加量的残差的绝对值 <br>
     * 
     * @param mdl 待计算的对象
     * @param dRemeberValue 记忆剩余量系数
     * @return 当前成熟度
     */
    public static double calcValidDegree(MemoryMDL mdl, double dRemeberValue) {
        return mdl.getValidDegree() * dRemeberValue + Math.abs(1 - mdl.getValidCount() * (1 - dRemeberValue));
    }
    
    /**
     * 计算候选项记忆剩余量
     * 
     * @param key 候选项
     * @param objMemoryItemColl 候选项集合
     * @return 返回记忆剩余量
     */
    public static double calcRemeberValue(String key, MemoryItemColl objMemoryItemColl)
    {
        if (!objMemoryItemColl.contains(key)) return 0;
        MemoryMDL mdl = objMemoryItemColl.get(key);
        double dRemeberValue = MemoryDAL.calcRemeberValue(objMemoryItemColl.getOffsetTotalCount() - mdl.getUpdateOffsetCount(), objMemoryItemColl.getMinuteOffsetSize());
        return mdl.getValidCount() * dRemeberValue;
    }

    /**
     * 计算邻键首项记忆剩余量
     * 
     * @param key 相邻两项的首项
     * @param objMemoryBondColl 邻键集合
     * @return 返回记忆剩余量
     */
    public static double calcRemeberValue(String key, MemoryBondColl objMemoryBondColl) {
        if (!objMemoryBondColl.contains(key)) {
            return 0;
        }
        MemoryBondMDL objBondMDL = objMemoryBondColl.get(key);
        MemoryMDL mdl = objBondMDL.getKeyItem();
        double dRemeberValue = MemoryDAL.calcRemeberValue(objMemoryBondColl.getOffsetTotalCount() - mdl.getUpdateOffsetCount(), objMemoryBondColl.getMinuteOffsetSize());
        return mdl.getValidCount() * dRemeberValue;
    }

    /**
     * 计算邻键尾项记忆剩余量
     * 
     * @param keyHead 相邻两项的首项
     * @param keyTail 相邻两项的尾项
     * @param objMemoryBondColl 邻键集合
     * @return 返回记忆剩余量
     */
    public static double CalcRemeberValue(String keyHead, String keyTail, MemoryBondColl objMemoryBondColl) {
        if (!objMemoryBondColl.contains(keyHead))
            return 0;
        MemoryBondMDL objBondMDL = objMemoryBondColl.get(keyHead);
        MemoryItemColl objLinkColl = objBondMDL.getLinkColl();
        return calcRemeberValue(keyTail, objLinkColl);
    }

    /**
     * 判断键是否为有效关联键，判断标准：共享键概率 > 单字概率之积
     * 
     * @param keyHead 相邻键中首项
     * @param keyTail 相邻键中尾项
     * @param objMemoryBondColl 相邻字典
     * @return 返回是否判断的结果：true、相邻项有关；false、相邻项无关
     */
    public static boolean isBondValid(String keyHead, String keyTail, MemoryBondColl objMemoryBondColl) {
        // 如果相邻项任何一个不在相邻字典中，则返回false 。
        if (!objMemoryBondColl.contains(keyHead) || !objMemoryBondColl.contains(keyTail)) {
            return false;
        }
        // 分别获得相邻单项的频次
        double dHeadValidCount = calcRemeberValue(keyHead, objMemoryBondColl);
        double dTailValidCount = calcRemeberValue(keyTail, objMemoryBondColl);
        // 获得相邻字典全库的总词频
        double dTotalValidCount = objMemoryBondColl.getMinuteOffsetSize();

        if (dTotalValidCount <= 0) {
            return false;
        }
        
        //获得相邻项共现的频次
        MemoryItemColl objLinkColl = objMemoryBondColl.get(keyHead).getLinkColl();
        if (!objLinkColl.contains(keyTail)) {
            return false;
        }
        double dShareValidCount = calcRemeberValue(keyTail, objLinkColl);

        //返回计算的结果
        return dShareValidCount / dHeadValidCount > dTailValidCount / dTotalValidCount;
    }
}
