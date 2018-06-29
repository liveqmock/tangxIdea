package com.topaiebiz.goods.sku.dto.app;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by dell on 2018/1/22.
 */
@Data
public class GoodsSkuCommentAppDto extends PagePO implements Serializable{

    /** 商品id。*/
    private Long itemId;

    /**0全部 1好评，2中评。3差评 4有图*/
    private Integer type;
}
