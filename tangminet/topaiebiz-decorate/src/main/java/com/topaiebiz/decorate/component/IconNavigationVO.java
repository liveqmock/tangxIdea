package com.topaiebiz.decorate.component;

import lombok.Data;

import java.util.List;

/**
 * 图片导航组件
 *
 * @author huzhenjia
 * @since 2018/03/29
 */
@Data
public class IconNavigationVO {

    private Integer iconCount;//图片数量

    private String fontColor;//字体颜色

    private String backgroundImage;//背景图片

    private List<ImageLinkVO> imageLinkVOS;
}
