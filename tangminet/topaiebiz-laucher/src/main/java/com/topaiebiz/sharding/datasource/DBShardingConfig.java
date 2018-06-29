package com.topaiebiz.sharding.datasource;

import com.google.common.base.Stopwatch;
import io.shardingjdbc.core.jdbc.core.datasource.ShardingDataSource;
import io.shardingjdbc.core.yaml.sharding.YamlShardingConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.topaiebiz.sharding.aop.DataSourceContextHolder.BIZ_DATASOURCE;
import static com.topaiebiz.sharding.aop.DataSourceContextHolder.LOG_DATASOURCE;

/**
 * Created by ward on 2018-02-03.
 */
@Slf4j
@Configuration
public class DBShardingConfig {

    @Autowired
    private ApplicationContext ctx;

    @Bean(name = LOG_DATASOURCE)
    public DataSource shardingLogDataSorce() throws Exception {
        Stopwatch st = Stopwatch.createStarted();
        Resource shardingConfig = ctx.getResource("classpath:sharding-jdbc.yml");
        YamlShardingConfiguration config = unmarshal(shardingConfig.getInputStream());
        log.info(">>>>>>>>>>>> 加载sharding-jdbc.yml 耗时:{} ms", st.elapsed(TimeUnit.MILLISECONDS));
        return new ShardingDataSource(config.getShardingRule(Collections.emptyMap()), config.getShardingRule().getConfigMap(), config.getShardingRule().getProps());
    }

    private static YamlShardingConfiguration unmarshal(InputStream is) throws IOException {
        return new Yaml(new Constructor(YamlShardingConfiguration.class)).loadAs(is, YamlShardingConfiguration.class);
    }

    @Bean(name = BIZ_DATASOURCE)
    public DataSource dataSource() {
        return new DruidDataSourceWrapper();
    }

}