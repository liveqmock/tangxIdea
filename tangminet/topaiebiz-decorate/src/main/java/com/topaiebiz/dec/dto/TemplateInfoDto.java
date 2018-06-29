package com.topaiebiz.dec.dto;


import lombok.Data;

/**
 * <p>
 * 装修模板信息表
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Data
public class TemplateInfoDto {


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
