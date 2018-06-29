package com.topaiebiz.sharding.aop;

import org.apache.commons.lang3.StringUtils;

/***
 * @author yfeng
 * @date 2018-02-05 18:54
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal();

    public static final String BIZ_DATASOURCE = "bizDataSource";
    public static final String LOG_DATASOURCE = "shardignLogDataSorce";

    public static void useShardingDataSource() {
        contextHolder.set(LOG_DATASOURCE);
    }

    public static String getDataSourceName() {
        String dataSourceName = contextHolder.get();
        if (StringUtils.isBlank(dataSourceName)){
            return BIZ_DATASOURCE;
        }
        return dataSourceName;
    }

    public static void clear() {
        contextHolder.remove();
    }
}