package com.gionee.client.business.statistic;

/**
 * 统计数据管理接口
 * 
 * @author yangxiong
 */
public interface IStatisticsEventManager {
    public void add(String eventId);

    public String buildStatisticsData();

    public void saveStatisticsData();

    public void removeStatisticsData();

    public void initStatisticsData();
}
