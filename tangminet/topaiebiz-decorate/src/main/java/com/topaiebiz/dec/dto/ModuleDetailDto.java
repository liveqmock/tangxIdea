package com.topaiebiz.dec.dto;

import lombok.Data;

@Data
public class ModuleDetailDto {
    /**
     * 全局唯一标识符。
     */
    private Long id;
    /**
     * 模块ID。
     */
    private Long moduleId;
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
