package com.topaiebiz.system.xiaoneng.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by ward on 2018-04-08.
 */
@Data
public class ItemPo {
    @JSONField(name = "itemid")
    private Long itemId;

    @JSONField(name = "itemparam")
    private String itemParam;

    private String userId;
}
