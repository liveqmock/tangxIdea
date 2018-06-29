package com.topaiebiz.member.address.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

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
@TableName("t_mem_member_address")
@Data
public class MemberAddressEntity extends BaseBizEntity<Long> {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -8224148444065736015L;

    /**
     * 会员编号。
     */
    private Long memberId;

    /**
     * 收货人。
     */
    private String name;

    /**
     * 性别。
     */
    private Integer gender;

    /**
     * 收货手机号。
     */
    private String telephone;

    /**
     * 座机号。
     */
    private String planeNumber;

    /**
     * 紧急联系电话。
     */
    private String otherTelephone;

    /**
     * 地址区域。
     */
    private Long districtId;

    /**
     * 详细地址。
     */
    private String address;

    /**
     * 邮编。
     */
    private String zipCode;

    /**
     * 是否默认地址（1 是，0不是）。
     */
    private Integer isDefault;

    /**
     * 是否常用地址（1 是，0不是）。
     */
    private Integer isCommon;

    /**
     * 是否公司地址（1 是，0不是）。
     */
    private Integer isCompany;

    /**
     * 是否家庭地址（1 是，0不是）。
     */
    private Integer isFamily;


}
