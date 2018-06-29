package com.topaiebiz.decorate.component;

import lombok.Data;

/**
 * 页面头部组件内容VO
 *
 * @author huzhenjia
 * @since 2018/03/29
 */
@Data
public class HeaderVO {

    private String titleName;//页面名称

    private Integer autoHidden;//自动隐藏

    private String fontColor;//字体颜色

    private Integer backgroundShowWay;//背景展示方式

    private String backgroundColor;//背景颜色

    private Integer isShareShow;//是否展示分享入口

    private String backgroundImage;//背景图片
}
