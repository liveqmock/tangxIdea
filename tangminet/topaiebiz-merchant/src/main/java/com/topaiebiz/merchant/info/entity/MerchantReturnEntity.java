package com.topaiebiz.merchant.info.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 商家退货表
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/22 0022 下午 12:35
 */
@TableName("t_mer_store_return")
@Data
public class MerchantReturnEntity extends BaseBizEntity<Long> {
    /**
     * 版本序列化
     */
    private static final long serialVersionUID = -9016636675413808094L;

    /**
     * 商家信息
     */
    private Long   merchantId;
    /**
     * 收货人姓名
     */
    private String consignee;

    /**
     * 收货人联系电话
     */
    private String contactNumber;

    /**
     * 收货地址
     */
    private Long districtId;

    /**
     * 详细地址
     */
    private String contactAddress;
}
