package com.topaiebiz.sharding.datasource;

import com.topaiebiz.sharding.aop.DataSourceContextHolder;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.util.Map;

import static com.topaiebiz.sharding.aop.DataSourceContextHolder.BIZ_DATASOURCE;
import static com.topaiebiz.sharding.aop.DataSourceContextHolder.LOG_DATASOURCE;

/***
 * @author yfeng
 * @date 2018-02-05 18:55
 */
@Component
@Primary
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Resource(name = BIZ_DATASOURCE)
    private DataSource coreBizDataSource;
    @Resource(name = LOG_DATASOURCE)
    private DataSource logDataSource;

    @Override
    public void afterPropertiesSet() {
        Map<Object, Object> targetDataSources = new HashedMap();
        targetDataSources.put(BIZ_DATASOURCE, coreBizDataSource);
        targetDataSources.put(LOG_DATASOURCE, logDataSource);
        setTargetDataSources(targetDataSources);

        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceName();
    }
}