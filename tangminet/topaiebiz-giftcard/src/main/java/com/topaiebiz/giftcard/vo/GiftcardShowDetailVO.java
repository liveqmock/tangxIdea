package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 礼卡详情展现
 * @author: Jeff Chen
 * @date: created in 下午1:54 2018/1/25
 */
@Data
public class GiftcardShowDetailVO  implements Serializable{
    /**
     *卡id
     */
    private Long batchId;

    /**
     * 订单提交必带，防止重复提交
     */
    private String orderKey;

    /**
     * 封面
     */
    private String cover;

    /**
     * 名称
     */
    private String cardName;

    /**
     * 副标题
     */
    private String subtitle;
    /**
     * spec
     * 备注描述
     */
    private String spec;
    /**
     * 面值
     */
    private BigDecimal faceValue;
    /**
     * 售价
     */
    private BigDecimal salePrice;

    /**
     * 剩余多张
     */
    private Integer restNum;
    /**
     * 限购数量
     */
    private Integer limitNum;


    /**
     * apply_scope
     * 适用范围：1 全部平台 2部分店铺可用 3 部分店铺不可用
     */
    private Integer applyScope;
    /**
     * 支持的店铺列表
     */
    private List<String> storeNameList;
    /**
     * 相同标签或属性下发行的不同面值礼卡
     */
    private List<IssueItemVO> itemList;
}
