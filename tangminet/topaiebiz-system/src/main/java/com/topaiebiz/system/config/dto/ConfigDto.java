package com.topaiebiz.system.config.dto;

import com.baomidou.mybatisplus.activerecord.Model;
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
public class ConfigDto{
	private Long id;
	private String configCode;
	private String configValue;
	private Long creatorId;
	private Date createdTime;
	private Long lastModifierId;
	private Date lastModifiedTime;
	private Integer deletedFlag;
	private Long version;


}
