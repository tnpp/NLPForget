package com.myproject.nlpdemo.service.forget.pojo;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryBondColl {
    private double offsetTotalCount = 0; // 偏移总量
    private double minuteOffsetSize = 7/* 每秒7个字符 */ * 60 * 60 * 24 /* 一天 */ * 6; // 最大记忆容量
    public Map<String, MemoryBondMDL> memoryBondMDL = new LinkedHashMap<>();

    public double getOffsetTotalCount() {
        return offsetTotalCount;
    }

    public void setOffsetTotalCount(double offsetTotalCount) {
        this.offsetTotalCount = offsetTotalCount;
    }

    public double getMinuteOffsetSize() {
        return minuteOffsetSize;
    }

    public void setMinuteOffsetSize(double minuteOffsetSize) {
        this.minuteOffsetSize = minuteOffsetSize;
    }


    public boolean contains(String key) {
        return memoryBondMDL.containsKey(key);
    }

    public void add(String key, MemoryBondMDL bond) {
        memoryBondMDL.put(key, bond);
    }

    public MemoryBondMDL get(String key) {
        return memoryBondMDL.get(key);
    }
}
