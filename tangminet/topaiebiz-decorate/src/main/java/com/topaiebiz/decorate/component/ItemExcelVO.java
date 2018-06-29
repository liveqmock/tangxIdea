package com.topaiebiz.decorate.component;

import lombok.Data;

/**
 * 商品EXCEL VO
 *
 * @author huzhenjia
 */
@Data
public class ItemExcelVO {

    private Long sortNo;//序列值

    private Long goodsId;//商品id

    private String itemName;//商品名称
}
