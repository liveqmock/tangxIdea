package com.topaiebiz.merchant.enter.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import com.topaiebiz.goods.dto.sku.StoreGoodsDTO;
import lombok.Data;

/**
 * Description: 店铺信息实体类
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月2日 下午7:30:25
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class StoreInfoDto extends PagePO implements Serializable {

    /**
     * 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;

    /**
     * 所属商家ID
     */
    private Long merchantId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 营销id集合
     */
    private List<Long> storeIdListByPromotionId;

    /**
     * 营销id
     */
    private Long promotionId;

    /**
     * 店铺名称
     */
    @NotNull(message = "{validation.storeInfo.name}")
    private String name;

    /**
     * 店铺类型
     */
    private String merchantType;

    /**
     * 实体店所在区域
     */
    @NotNull(message = "{validation.storeInfo.districtId}")
    private Long districtId;

    /**
     * 实体店位置
     */
    @NotNull(message = "{validation.storeInfo.storeAddress}")
    private String storeAddress;

    /**
     * 商家联系人姓名
     */
    @NotNull(message = "{validation.storeInfo.contactName}")
    private String contactName;

    /**
     * 联系人手机号
     */
    @NotNull(message = "{validation.storeInfo.contactTele}")
    private String contactTele;

    /**
     * 门店电话
     */
    @NotNull(message = "{validation.storeInfo.storeTele}")
    private String storeTele;

    /**
     * 商家介绍
     */
    private String description;

    /**
     * 地理位置
     */
    private String position;

    /**
     * 门店照片多张
     */
    @NotNull(message = "{validation.storeInfo.images}")
    private String images;

    /**
     * 店铺等级。和商家保持一致
     */
    private Long merchantGradeId;

    /**
     * 类目的List
     */
    private List<BackendCategorysDTO> backendCategorysDtos;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 等级名称
     */
    private String merchantGradeName;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺log
     */
    private String imgages;

    /**
     * 店铺商品
     */
    private List<StoreGoodsDTO> storeGoods;

    /**
     * 排序
     */
    private Long displayOrder;

    /**
     * 银行账号
     */
    private String settleAccount;

    /**
     * 开户人姓名
     */
    private String settleAccountName;

    /**
     * 开户银行
     */
    private String settleBankName;

    /**
     * 开户行地址
     */
    private Long settleBankDistrictId;

    /**
     * 开户支行
     */
    private String settleBankNum;

    /**
     * 结算周期:月，半月，周，5天
     */
    private String settleCycle;

    /**
     * 入驻时间
     */
    private Date createdTime;
}
