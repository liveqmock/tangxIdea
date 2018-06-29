package com.topaiebiz.decorate.component;

import lombok.Data;

/**
 * 文字链接VO
 *
 * @author huzhenjia
 * @since
 */
@Data
public class WordsLinkVO {

    private String words;

    private String fontColor;

    private Integer linkType;

    private String linkValue;

    private Long sortNo;
}
