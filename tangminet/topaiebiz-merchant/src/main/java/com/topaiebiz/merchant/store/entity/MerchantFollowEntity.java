package com.topaiebiz.merchant.store.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import com.nebulapaas.data.mybatis.common.IdEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/19 0019 上午 10:27
 */
@TableName("t_mem_merchant_follow")
@Data
public class MerchantFollowEntity extends IdEntity<Long> {

    /**
     * 版本序列化
     */
    private static final long serialVersionUID = -9016636675413808094L;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 店铺
     */
    private Long storeId;

    private Long creatorId;
    private Date createdTime = new Date();

    private Byte deletedFlag = 0;
    @Version
    private Long version = 1L;
    public void clearInit() {
        this.setVersion(null);
        this.setCreatedTime(null);
    }

}
