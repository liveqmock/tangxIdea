package com.topaiebiz.merchant.dto.store;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StoreInfoDetailDTO {

    /**
     * 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;

    /**
     * 所属商家ID
     */
    private Long merchantId;

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
     * 商家名称
     */
    private String MerchantName;

    /**
     * 店铺状态 2冻结
     */
    private Integer changeState;

    /**
     * 是否直营店铺
     */
    private Integer ownShop;
    /**
     * 积分支付比例(新店铺为默认100)
     */
    private BigDecimal ptRate;

    /**
     * 海淘标识，1为是，0为否。
     */
    private Integer haitao;

    /**
     * 结算周期:月，半月，周，5天
     */
    private String settleCycle;

    /**
     * 下个结算日。
     */
    private Date nextSettleDate;

}