package com.topaiebiz.dec.dto;

import lombok.Data;

import java.util.List;

@Data
public class AppHomePageDto {

    private Long moduleId;

    private Long parentId;

    private List<AppModuleInfoDto> moduleInfoList;

    private List<TemplateTitleDto> titleList;

  //  private AppTitleGoodsDto appTitleGoodsDto;

    private AppModuleGoodsDto appModuleGoodsDto;

}
