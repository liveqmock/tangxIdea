package com.topaiebiz.decorate.component;

import lombok.Data;

/**
 * 图片链接VO
 *
 * @author huzhenjia
 * @since 2018/03/29
 */
@Data
public class ImageLinkVO {

    private String image;//图片

    private String words;//文字

    private Integer linkType;//链接类型

    private String linkValue;//链接值

    private Long sortNo;//权重值
}
