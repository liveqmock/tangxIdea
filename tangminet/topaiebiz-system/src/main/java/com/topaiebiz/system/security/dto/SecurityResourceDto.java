package com.topaiebiz.system.security.dto;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class SecurityResourceDto {

    private Long id;

    /** '系统资源名称（例如会员管理、添加）。' */
    private String name;

    /** '父资源ID（一级菜单则为0）' */
    private Long parentId;

    /** '系统资源排序号。' */
    private String sortNumber;

    /**访问URL*/
    private String accessUrl;

    //是否选中  0为未选中
    private Integer isCheck = 0;

    /**
     * 子资源
     */
    private List<SecurityResourceDto> childList;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SecurityResourceDto that = (SecurityResourceDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
