package com.topaiebiz.system.xiaoneng.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by ward on 2018-04-05.
 */
@Data
public class NtkfParamGoodsDto extends NtkfParamCommonDto {

    /**
     * itemid：(必填)商品ID
     */
    @JSONField(name = "itemid")
    private Long itemId;

    /**
     * itemparam：(选填)itemparam为商品接口扩展字段，用于商品接口特殊要求集成
     */

    @JSONField(name = "itemparam")
    private String itemParam;
}
