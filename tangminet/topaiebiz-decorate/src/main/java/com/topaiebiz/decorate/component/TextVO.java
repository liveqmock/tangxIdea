package com.topaiebiz.decorate.component;

import lombok.Data;

/**
 * 文本组件VO
 * @author huzhenjia
 * @since 2018/03/29
 */
@Data
public class TextVO {

    private String text;

    private Integer fontSize;

    private String fontColor;

    private String backgroundColor;

    private Integer typeSet;//文字排版


    private Integer linkType;

    private String linkValue;
}
