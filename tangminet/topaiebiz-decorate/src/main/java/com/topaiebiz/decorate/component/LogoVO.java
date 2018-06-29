package com.topaiebiz.decorate.component;

import lombok.Data;

@Data
public class LogoVO {

    private String optionLogo;//选项logo

    private String selectedLogo;//选中时logo

    private Integer linkType;//链接类型

    private String linkValue;//链接值

    private Long sortNo;
}
