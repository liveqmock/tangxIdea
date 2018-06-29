package com.topaiebiz.monitor.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

@TableName("t_mon_monitor_log")
@Data
public class MonitorLogEntity extends BaseBizEntity<Long> {
    private static final long serialVersionUID = -2359021431922258073L;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 日志内容
     */
    private String content;
}
