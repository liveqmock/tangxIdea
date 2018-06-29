package com.topaiebiz.monitor.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

@TableName("t_mon_monitor_performance")
@Data
public class MonitorPerformanceEntity extends BaseBizEntity<Long> {
    private static final long serialVersionUID = -311508704906741036L;
    /**
     * 请求IP
     */
    private String reqIp;
    /**
     * 请求方式：POST、GET、PUT、DELETE
     */
    private String reqMethod;
    /**
     * 请求头Referer
     */
    private String reqRef;
    /**
     * 请求头User-Agent。
     */
    private String reqUa;
    /**
     * 请求路径
     */
    private String reqPath;
    /**
     * 请求耗时（ms）
     */
    private Long spend;
}
