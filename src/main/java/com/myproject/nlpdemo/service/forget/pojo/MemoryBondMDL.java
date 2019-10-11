package com.myproject.nlpdemo.service.forget.pojo;

public class MemoryBondMDL {
    private MemoryMDL keyItem = new MemoryMDL(); // 主项
    private MemoryItemColl linkColl = new MemoryItemColl(); // 关联项集合

    public MemoryMDL getKeyItem() {
        return keyItem;
    }

    public void setKeyItem(MemoryMDL keyItem) {
        this.keyItem = keyItem;
    }

    public MemoryItemColl getLinkColl() {
        return linkColl;
    }

    public void setLinkColl(MemoryItemColl linkColl) {
        this.linkColl = linkColl;
    }
}
