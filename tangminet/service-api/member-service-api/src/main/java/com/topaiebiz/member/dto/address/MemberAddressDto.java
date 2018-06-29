package com.topaiebiz.member.dto.address;


import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Description：会员收货地址,存储会员的收货地址。
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年10月13日 下午7:59:05
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MemberAddressDto {

    /**
     * 会员等级的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;

    /**
     * 会员编号。
     */
    private Long memberId;

    /**
     * 收货人。
     */
    @NotBlank(message = "{validation.address.name}")
    @Length(min = 2, max = 16, message = "{validation.address.name}")

    private String name;

    /**
     * 收货手机号。
     */
    @NotNull(message = "{validation.address.telephone}")
    @Length(min = 11, max = 11)
    private String telephone;

    /**
     * 地址区域。
     */
    @NotNull(message = "{validation.address.districtId}")
    private Long districtId;

    /**
     * 具体区域
     */
    //private String districtValue;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 省id
     */
    private Long provinceId;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 市id
     */
    private Long cityId;

    /**
     * 区名称
     */
    private String districtName;

    /**
     * 详细地址。
     */
    @NotBlank(message = "{validation.address.address}")
    @Length(min = 5, max = 1000, message = "详细地址：长度要在5到100之间")
    private String address;

    /**
     * 是否默认地址（1 是，0不是）。
     */
    private Integer isDefault = 0;

    /**
     * 性别 性别（1 男  0 女)
     */
    private Integer gender;

    private String otherTelephone;

    private String zipCode;

}