package com.topaiebiz.dec.dto;


import lombok.Data;

import java.util.List;


/**
 * <p>
 * 商品标题表
 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
@Data
public class TemplateTitleDto {

    /**
     * 全局唯一标识符。
     */
    private Long id;
    /**
     * 模块ID。
     */
    private Long moduleId;
    /**
     * 一级标题名称
     */
    private String titleName;
    /**
     * 二级标题。
     */
    private List<SecondTitleDto> secondTitleDto;

    /**
     * 显示顺序。
     */
    private Long sortNo;
    /**
     * 备注。
     */
    private String memo;

    /**
     * 标题等级
     */
    private Integer level;
}
