package com.topaiebiz.dec.dto;

import lombok.Data;

@Data
public class ModifyTitleDto {

    private Long id;

    private Long targetId;

    private String titleName;
}
