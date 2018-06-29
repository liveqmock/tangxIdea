package com.topaiebiz.system.security.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import com.topaiebiz.system.annotation.PermitType;
import lombok.Data;

@TableName(value = "t_sys_resource_collect")
@Data
public class ResourceCollectEntity extends BaseBizEntity<Long> {

    /**操作名称*/
    private String operationName;

    /**URL*/
    private String URL;

    /**'所属平台'*/
    private PermitType permitType;

}
