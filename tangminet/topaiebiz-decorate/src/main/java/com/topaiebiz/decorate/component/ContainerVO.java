package com.topaiebiz.decorate.component;

import lombok.Data;

import java.util.List;

/**
 * 页面容器VO
 *
 * @author huzhenjia
 * @since 2018/03/30
 */
@Data
public class ContainerVO {

    private Integer backgroundShowWay;

    private String optionColor;

    private String selectedColor;

    private Integer foldSwitch;

    private List<TabVO> tabVOS;
}
