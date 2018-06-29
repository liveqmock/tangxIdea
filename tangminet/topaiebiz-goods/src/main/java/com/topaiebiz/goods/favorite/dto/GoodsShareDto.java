package com.topaiebiz.goods.favorite.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 我的分享dto
 *
 * Author Hedda
 *
 * Date 2017年11月16日 下午4:54:37
 *
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class GoodsShareDto implements Serializable{

    /** 全局唯一主键标识符。支持泛型，具体类型由传入的类型指定。 */
    private Long id;

    /** 会员id*/
    private Long memberId;

    /** 商品id*/
    private Long goodsId;
}
