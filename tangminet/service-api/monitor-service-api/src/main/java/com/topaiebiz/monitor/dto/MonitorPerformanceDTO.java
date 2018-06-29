package com.topaiebiz.monitor.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MonitorPerformanceDTO implements Serializable {
    private static final long serialVersionUID = 1503340935025117405L;
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
