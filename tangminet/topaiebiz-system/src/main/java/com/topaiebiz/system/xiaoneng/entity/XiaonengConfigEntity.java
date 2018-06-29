package com.topaiebiz.system.xiaoneng.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description：小能客服配置表
 * <p>
 * Author Ward.Wang
 * <p>
 * Date 2017年10月13日 下午7:59:05
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_sys_xiaoneng_config")
@Data
public class XiaonengConfigEntity extends BaseBizEntity<Long> {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -8224148444065736015L;

    /**
     * 编号。
     */
    private Long id;

    /**
     * 店铺ID
     */
    private Long storeId;


    /**
     * 小能店铺ID
     */
    private String ntkfSellerId;

    /**
     * 售前客服配置
     */
    private String preSalesConfig;

    /**
     * 售后客服配置
     */
    private String afterSalesConfig;

    /**
     * 创建人编号。取值为创建人的全局唯一主键标识符。
     */
    private Long creatorId;

    /**
     * 创建时间。默认取值为系统的当前时间。
     */
    private Date createdTime;

    /**
     * 最后修改人编号。取值为最后修改人的全局唯一主键标识符。
     */
    private Long lastModifierId;

    /**
     * 最后修改时间。默认取值为null，当发生修改时取系统的当前时间。
     */
    private Date lastModifiedTime;

    /**
     * 逻辑删除标识。仅且仅有0和1两个值，1表示已经被逻辑删除，0表示正常可用，默认为0。
     */
    private Byte deletedFlag;

    /**
     * 版本号。信息的版本号。乐观锁机制的辅助字段，用于控制信息的一致性。默认取值为1，执行更新操作时，自动加1。
     */
    @Version
    private Long version;


}
