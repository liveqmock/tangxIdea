package com.topaiebiz.merchant.info.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商家退货信息dto
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/22 0022 下午 12:34
 */
@Data
public class MerchantReturnDto implements Serializable {


    /**
     * 商家信息
     */
    private Long   merchantId;
    /**
     * 收货人姓名
     */
    @NotNull(message = "{validation.merchantReturn.consignee}")
    private String consignee;

    /**
     * 收货人联系电话
     */
  //  @NotNull(message = "{validation.merchantReturn.contactNumber}")
    //@Length(min = 11, max = 11)
    private String contactNumber;

    /**
     * 收货地址
     */
    @NotNull(message = "{validation.merchantReturn.districtId}")
    private Long districtId;

    /**
     * 详细地址
     */
    private String contactAddress;

}
