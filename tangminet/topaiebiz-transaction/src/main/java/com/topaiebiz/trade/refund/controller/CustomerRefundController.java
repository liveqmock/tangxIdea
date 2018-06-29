package com.topaiebiz.trade.refund.controller;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BindResultUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.trade.refund.dto.RefundApplyParamDTO;
import com.topaiebiz.trade.refund.dto.RefundLogisticsDTO;
import com.topaiebiz.trade.refund.dto.RefundSubmitDTO;
import com.topaiebiz.trade.refund.enumdata.RefundReasonEnum;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.trade.refund.service.CustomerRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Description 售后控制器
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/8 12:16
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@MemberLogin
@RestController
@Slf4j
@RequestMapping(value = "/trade/refund/customer", method = RequestMethod.POST)
public class CustomerRefundController {

    @Autowired
    private CustomerRefundService customerRefundService;

    /**
     * Description: 获取用户得售后列表
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    @RequestMapping(value = "/getCustomerRefundOrderPage")
    public ResponseInfo getCustomerRefundOrderPage(@RequestBody PagePO pagePO) {
        return new ResponseInfo(customerRefundService.getCustomerRefundOrderPage(pagePO));
    }

    /**
     * Description: 用户申请售后
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    @RequestMapping(value = "/applyFofRefund")
    public ResponseInfo applyFofRefund(@RequestBody RefundApplyParamDTO refundApplyParamDTO) {
        if (null == refundApplyParamDTO.getRefundId()) {
            if (CollectionUtils.isEmpty(refundApplyParamDTO.getOrderDetailIds()) && null == refundApplyParamDTO.getOrderId()) {
                throw new GlobalException(RefundOrderExceptionEnum.REFUND_GOODS_CANT_BE_NULL);
            }
        }
        return new ResponseInfo(customerRefundService.applyForRefund(refundApplyParamDTO));
    }

    /**
     * Description: 用户售后被拒绝之后, 重新申请售后
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/26
     *
     * @param:
     **/
    @RequestMapping(value = "/reapply/{refundId}")
    public ResponseInfo reapply(@PathVariable Long refundId) {
        return new ResponseInfo(customerRefundService.reapply(refundId));
    }


    /**
     * Description: 提交申请的售后
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    @RequestMapping(value = "/submitApplyFofRefund")
    public ResponseInfo submitAplyofRefund(@RequestBody @Valid RefundSubmitDTO refundSubmitDTO, BindingResult bindingResult) {
        BindResultUtil.dealBindResult(bindingResult);
        Integer refundType = refundSubmitDTO.getRefundType();
        if (!refundType.equals(Constants.Refund.REFUND) && !refundType.equals(Constants.Refund.RETURNS)) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_TYPE_IS_NOT_ALLOWABLE);
        }
        if (null == refundSubmitDTO.getRefundId() && null == refundSubmitDTO.getOrderId()) {
            throw new GlobalException(RefundOrderExceptionEnum.SUBMIT_REFUND_PARAMS_ILLEGAL);
        }
        if (!RefundReasonEnum.inValues(refundSubmitDTO.getRefundReasonCode())) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_REASON_CODE_IS_NOT_ALLOWABLE);
        }
        // 重新申请
        if (null != refundSubmitDTO.getIfReapply() && refundSubmitDTO.getIfReapply()) {
            return new ResponseInfo(customerRefundService.submitReapply(refundSubmitDTO));
        } else {
            return new ResponseInfo(customerRefundService.refundSubmit(refundSubmitDTO));
        }
    }

    /**
     * Description: 获取售后订单的详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    @RequestMapping(value = "/refundDetail/{refundOrderId}")
    public ResponseInfo refundDetail(@PathVariable Long refundOrderId) {
        return new ResponseInfo(customerRefundService.getRefundDetailInfo(refundOrderId));
    }


    /**
     * Description: 提交物流信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/28
     *
     * @param:
     **/
    @RequestMapping(value = "/submitLogisticsInfo")
    public ResponseInfo submitLogisticsInfo(@Valid @RequestBody RefundLogisticsDTO refundLogisticsDTO, BindingResult bindingResult) {
        BindResultUtil.dealBindResult(bindingResult);
        return new ResponseInfo(customerRefundService.submitLogisticsInfo(refundLogisticsDTO));
    }

    /**
     * Description: 取消售后
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/28
     *
     * @param:
     **/
    @RequestMapping(value = "/cancel/{refundOrderId}")
    public ResponseInfo cancelRefundOrder(@PathVariable Long refundOrderId) {
        return new ResponseInfo(customerRefundService.cancelRefundOrder(refundOrderId));
    }

    /**
     * Description: 平台介入
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/28
     *
     * @param:
     **/
    @RequestMapping(value = "/platformInvolve/{refundOrderId}")
    public ResponseInfo platformInvolve(@PathVariable Long refundOrderId) {
        return new ResponseInfo(customerRefundService.platformInvolve(refundOrderId));
    }


    /**
     * Description: 售后原因 集合
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/9
     *
     * @param:
     **/
    @RequestMapping(value = "/refundReason")
    public ResponseInfo refundReason() {
        return new ResponseInfo(RefundReasonEnum.getMap());
    }

}
