package com.topaiebiz.goods.dto.sku;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * Created by hecaifeng on 2018/5/15.
 */
@Data
public class ApiGoodsQueryDTO extends PagePO {

    /**
     * 商品itemId
     */
    private Long itemId;

    /**
     * 商品item名称(标题显示的名称)。
     */
    private String itemName;

    /**
     * 商品状态（1 仓库中 2 已上架 3 下架 ）。
     */
    private Integer itemStatus;

    /**
     * 店铺Id。
     */
    private Long storeId;
}
