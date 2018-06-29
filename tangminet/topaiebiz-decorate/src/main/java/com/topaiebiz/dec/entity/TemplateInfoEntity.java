package com.topaiebiz.dec.entity;

import java.util.Date;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 装修模板信息表
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Data
@TableName("t_dec_template_info")
public class TemplateInfoEntity extends BaseBizEntity<Long> {


    /**
     * 全局唯一标识符。
     */
	private Long id;
    /**
     * 店铺ID。
     */
	private Long storeId;
    /**
     * 模版名称。
     */
	private String templateName;
    /**
     * 是否选用。1启用 0禁用
     */
	private String isUsed;
    /**
     * 模版ID。
     */
	private Long templateId;
    /**
     * 备注。
     */
	private String memo;


}
