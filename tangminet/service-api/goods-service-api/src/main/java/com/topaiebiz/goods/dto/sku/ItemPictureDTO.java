package com.topaiebiz.goods.dto.sku;

import com.topaiebiz.goods.constants.GoodsConstants;
import lombok.Data;

import java.io.Serializable;

/**
 * Description 商品图片，存储商品的图片信息  。
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月23日 下午5:23:47
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class ItemPictureDTO implements Serializable {

    /**
     * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
     */
    private Long id;

    /**
     * 所属商品。
     */
    private Long itemId;

    /**
     * 图片名称。
     */
    private String name;

    /**
     * 图片类型（1为显示的5张主图，2 为详情图）。
     */
    private Integer type;

    /**
     * 是否为主图（1是 ,0否，空也为否）。
     */
    private Integer isMain;

    public boolean isMainPicture() {
        return GoodsConstants.PicType.MAIN_PIC.equals(isMain);
    }

    public boolean isDetailPicture() {
        return GoodsConstants.PicType.DETAIL_PIC.equals(isMain);
    }
}