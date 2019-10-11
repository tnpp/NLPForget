package com.myproject.nlpdemo.service.forget;

/**
 * 遗忘算法 (牛顿冷却定律)
 * 
 * @author qux
 *
 */
public class MemoryDAL {
    /**
     * 牛顿冷却公式
     * 
     * @param parameter
     *            冷却系数
     * @param interval
     *            时间间隔
     * @return
     */
    public static double calcNetonCooling(double parameter, double interval) {
        return Math.exp(-1 * parameter * interval);
    }

    /**
     * 遗忘公式（调用牛顿冷却），以偏移量模拟时间流逝的遗忘公式<br>
     * 此处第二个参数没有使用，原因是已经将相关计算合并进遗忘系数中，此处保留为兼容代码。<br>
     * 使用艾宾浩斯曲线计算系数时是以分钟为单位，此处的系数已经转换为按字符计时，故无需再将字符转时间。
     * 
     * @param dOffsetCount 偏移量
     * @param dMinuteOffsetSize 单个周期容量，建议值为：6天 * 24小时 * 60分钟 *60秒 *7每秒阅读字数
     * @return 返回记忆剩余量
     */
    public static double calcRemeberValue(double dOffsetCount, double dMinuteOffsetSize) {
        // double parameter = -Math.log(0.254, Math.E) / (6 * 24 * 60 * 60 * 7);
        double parameter = -Math.log(0.254) / (6 * 24 * 60 * 60 * 7); // java的log是以e(2.71828...)为底来计算的
        return calcNetonCooling(parameter, dOffsetCount);
    }
}
