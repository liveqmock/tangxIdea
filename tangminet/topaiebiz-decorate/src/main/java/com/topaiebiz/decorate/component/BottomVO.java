package com.topaiebiz.decorate.component;

import lombok.Data;

import java.util.List;

@Data
public class BottomVO {

    private Integer backgroundShowWay;

    private String backgroundColor;

    private List<LogoVO> logoVOS;
}
