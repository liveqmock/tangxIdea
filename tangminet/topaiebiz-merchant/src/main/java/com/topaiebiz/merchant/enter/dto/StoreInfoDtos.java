package com.topaiebiz.merchant.enter.dto;

import com.topaiebiz.goods.dto.category.backend.BackendCategoryStatusDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class StoreInfoDtos implements Serializable {

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
    private String name;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 选择店铺类型
     */
   // @NotNull(message = "{validation.storeInfo.merchantType}")
    private Integer merchantType;

    /**
     * 选择商品的第三极类目
     */
    private Long[] ids;
    private List<BackendCategoryStatusDTO> backendCategoryStatusDTOS;

    /**
     * 商家L
     */
    private String imgages;

    /**
     * 商家名称
     */
    private String storeName;

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
     * 商家类目审核是否通过，1为审核通过。0为待审核。
     */
    private Integer status;

}
