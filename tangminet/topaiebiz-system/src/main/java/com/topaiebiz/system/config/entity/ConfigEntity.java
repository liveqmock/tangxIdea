package com.topaiebiz.system.config.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统公共配置表
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Data
@TableName("t_sys_config")
public class ConfigEntity extends BaseBizEntity<Long> {
	private Long id;
	private String configCode;
	private String configValue;

}
