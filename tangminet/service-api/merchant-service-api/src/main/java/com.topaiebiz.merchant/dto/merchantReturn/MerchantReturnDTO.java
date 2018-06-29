package com.topaiebiz.merchant.dto.merchantReturn;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 商家店铺退货dto
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/2/8 0008 下午 7:33
 */
@Data
public class MerchantReturnDTO {

    /**
     * 商家信息
     */
    private Long   merchantId;
    /**
     * 收货人姓名
     */
   // @NotNull(message = "{validation.merchantReturn.consignee}")
    private String consignee;

    /**
     * 收货人联系电话
     */
  //  @NotNull(message = "{validation.merchantReturn.contactNumber}")
  //  @Length(min = 11, max = 11)
    private String contactNumber;

    /**
     * 收货地址
     */
   // @NotNull(message = "{validation.merchantReturn.districtId}")
    private Long districtId;

    /**
     * 区域地址中文名称
     */
    private String districtStr;

    /**
     * 详细地址
     */
    private String contactAddress;
    /**
     * 店铺id
     */
    private Long storeId;

}
