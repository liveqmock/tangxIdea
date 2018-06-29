package com.topaiebiz.system.security.dto;

import lombok.Data;

import java.util.Set;

/**
 * 返回前台的DTO
 */
@Data
public class SecurityUserDto {

    private String userLoginId;

    private String username;

    private String mobilePhone;

    private Long merchantId;

    private Long storeId;

    private Set<SecurityResourceDto> resoucesSet;

}
