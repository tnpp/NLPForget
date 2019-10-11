package com.myproject.nlpdemo.service.forget.pojo;

public class MemoryMDL {
    private double validCount = 0; // 遗忘累频
    private double totalCount = 0; // 累计次数
    private double updateOffsetCount = 0; // 最后一次更新时的系统总偏移量（用于模拟计时）
    private double validDegree = 1; // 有效程度（成熟度）成熟度的物理含义：成熟的标志是遗忘的量与出现的量基本一致

    public double getValidCount() {
        return validCount;
    }

    public void setValidCount(double validCount) {
        this.validCount = validCount;
    }

    public double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(double totalCount) {
        this.totalCount = totalCount;
    }

    public double getUpdateOffsetCount() {
        return updateOffsetCount;
    }

    public void setUpdateOffsetCount(double updateOffsetCount) {
        this.updateOffsetCount = updateOffsetCount;
    }

    public double getValidDegree() {
        return validDegree;
    }

    public void setValidDegree(double validDegree) {
        this.validDegree = validDegree;
    }

}
