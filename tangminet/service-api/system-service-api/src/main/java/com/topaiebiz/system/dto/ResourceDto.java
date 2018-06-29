package com.topaiebiz.system.dto;

import com.topaiebiz.system.annotation.PermitType;
import lombok.Data;

/**
 * 资源DTO
 */
@Data
public class ResourceDto {

    private String operationName;

    private String URL;

    private PermitType permitType;
}
