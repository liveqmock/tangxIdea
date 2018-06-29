package com.topaiebiz.dec.dto;


import lombok.Data;

/**
 * <p>
 * 装修模板模块表
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Data
public class TemplateModuleDto {

    /**
     * 全局唯一标识符。
     */
	private Long id;
    /**
     * 所属模版信息。
     */
	private Long infoId;
    /**
     * 装饰类型。1为普通的  2为商品标题
     */
	private String decType;
    /**
     * 父模块ID。
     */
	private Long parentId;
    /**
     * 标题。
     */
	private String title;
    /**
     * 图标图片
     */
	private String iconImage;
    /**
     * 备注。
     */
	private String memo;

}
