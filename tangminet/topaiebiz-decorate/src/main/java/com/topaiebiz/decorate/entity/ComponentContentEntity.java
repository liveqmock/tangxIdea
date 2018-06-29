package com.topaiebiz.decorate.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

@Data
@TableName("t_dec_component_content")
public class ComponentContentEntity extends BaseBizEntity<Long> {


    private Long componentId;//页面组件关联表主键id

    private String type;//组件类型

    private String content;//组件内容

}
