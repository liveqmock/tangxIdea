package com.topaiebiz.monitor.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MonitorLogDTO implements Serializable {
    private static final long serialVersionUID = -4820808324434803197L;

    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 日志内容
     */
    private String content;
}
