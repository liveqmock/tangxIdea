package com.topaiebiz.merchant.info.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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
@TableName("t_mer_store_info")
public class StoreInfoEntity extends BaseBizEntity<Long> {

    /**
     * 版本序列化
     */
    private static final long serialVersionUID = -9016636675413808094L;

    /**
     * 所属商家ID
     */
    private Long merchantId;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺模板
     */
    private Long templateId;

    /**
     * 实体店所在区域
     */
    private String districtId;

    /**
     * 实体店位置
     */
    private String storeAddress;

    /**
     * 店铺的积分。和后期奖惩有关系
     */
    private Long integral;

    /**
     * 店铺等级积分
     */
    private Long gradeIntegral;

    /**
     * 店铺等级。和商家保持一致
     */
    private Long merchantGradeId;

    /**
     * 商家联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactTele;

    /**
     * 门店电话
     */
    private String storeTele;

    /**
     * 商家介绍
     */
    private String description;

    /**
     * 门店照片多张
     */
    private String images;

    /**
     * 地理位置。
     */
    private String position;

    @TableField(exist = false)
    private boolean flag;

    /**
     * 店铺状态 2冻结
     */
    private Integer changeState;

    /**
     * 会员id
     */
    private Long memberId;

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
     * 排序
     */
    private Long displayOrder;

    /**
     * 结算周期:月，半月，周，5天
     */
    private String settleCycle;

    /**
     * 下个结算日。
     */
    private Date nextSettleDate;
}
