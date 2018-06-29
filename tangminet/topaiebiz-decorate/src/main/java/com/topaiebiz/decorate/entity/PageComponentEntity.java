package com.topaiebiz.decorate.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 页面组件关联实体
 * @author huzhenjia
 * @Since 2018/03/26
 */
@Data
@TableName("t_dec_page_component")
public class PageComponentEntity extends BaseBizEntity<Long> {


    private String name;//组件名称

    private Long pageId;//关联页面id

    private Long sortNo;//权重值

    private String type;//组件类型

}
