package com.topaiebiz.system.security.dto;

import lombok.Data;

/**
 *  返回前台的角色DTO
 */
@Data
public class SecurityRoleDto {

    /** 系统角色编号。 */
    private Long id;

    /** 系统角色名称。 */
    private String name;

    /** 系统角色的描述信息。 */
    private String description;

    /** 系统角色的内置标识。仅且仅有0和1两个值，1表示内置角色，0表示非内置角色 注意，内置角色不能被删除。 */
    private Byte inbuiltFlag;
}
