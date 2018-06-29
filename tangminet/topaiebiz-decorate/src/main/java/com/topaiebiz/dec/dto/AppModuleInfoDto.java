package com.topaiebiz.dec.dto;


import lombok.Data;

/**
 * <p>
 * 模块信息详情表

 * </p>
 *
 * @author  hzj
 * @since 2018-01-08
 */
@Data
public class AppModuleInfoDto {

    /**
     * 全局唯一标识符。
     */
	private Long id;

    /**
     * 图片地址。
     */
	private String image;
    /**
     * 链接类型。1商品  2类目  3品牌  4 自定义url
     */
	private Integer jumpType;
    /**
     * 跳转结果。
     */
	private String jumpValue;
    /**
     * 排序号。
     */
	private Long sortNo;
    /**
     * 备注。
     */
	private String memo;

}
