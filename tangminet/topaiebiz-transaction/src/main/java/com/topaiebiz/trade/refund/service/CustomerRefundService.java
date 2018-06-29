package com.topaiebiz.trade.refund.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.trade.refund.dto.RefundApplyParamDTO;
import com.topaiebiz.trade.refund.dto.RefundLogisticsDTO;
import com.topaiebiz.trade.refund.dto.RefundSubmitDTO;
import com.topaiebiz.trade.refund.dto.detail.CustomerRefundDetailDTO;
import com.topaiebiz.trade.refund.dto.page.CustomerRefundPageDTO;

/**
 * Description 用户售后服务层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/8 12:29
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface CustomerRefundService {


    /**
     * Description: 获取用户的售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/8
     *
     * @param: pagePO 分页参数
     * memberId 当前登录人ID
     **/
    PageInfo<CustomerRefundPageDTO> getCustomerRefundOrderPage(PagePO pagePO);

    /**
     * Description: 申请售后
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    RefundSubmitDTO applyForRefund(RefundApplyParamDTO refundApplyParamDTO);

    /**
     * Description: 重新申请
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/24
     *
     * @param:
     **/
    RefundSubmitDTO reapply(Long refundId);

    /**
     * Description: 提交售后申请
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    Boolean refundSubmit(RefundSubmitDTO refundSubmitDTO);


    /**
    *
    * Description: 提交 重新申请的售后信息
    *
    * Author: hxpeng
    * createTime: 2018/4/12
    *
    * @param:
    **/
    boolean submitReapply(RefundSubmitDTO refundSubmitDTO);

    /**
     * Description: 获取售后详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    CustomerRefundDetailDTO getRefundDetailInfo(Long refundOrderId);

    /**
     * Description: 提交寄回商品的物流信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    Boolean submitLogisticsInfo(RefundLogisticsDTO refundLogisticsDTO);

    /**
     * Description: 取消售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    Boolean cancelRefundOrder(Long refundOrderId);

    /**
     * Description: 需要平台介入(申诉)
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/17
     *
     * @param:
     **/
    Boolean platformInvolve(Long refundOrderId);

}
