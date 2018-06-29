package com.topaiebiz.dec.dto;

import lombok.Data;

import java.util.List;

@Data
public class ModuleInfoDto {

    private Long moduleId;

    private List<ModuleDetailDto> moduleDetailDtoList;
}
