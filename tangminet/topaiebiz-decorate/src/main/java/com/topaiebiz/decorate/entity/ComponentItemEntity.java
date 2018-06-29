package com.topaiebiz.decorate.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

@Data
@TableName("t_dec_component_item")
public class ComponentItemEntity extends BaseBizEntity<Long> {

    private Long componentId;

    private Long itemId;
}
