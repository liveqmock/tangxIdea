package com.topaiebiz.transaction.payment.log.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.transaction.payment.log.dto.PayOrderLogDTO;
import com.topaiebiz.transaction.payment.log.entity.PayOrderLogEntity;
import com.topaiebiz.transaction.payment.log.exception.PayOrderLogExceptionEnum;
import com.topaiebiz.transaction.payment.log.service.PayOrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description 第三方支付记录展现层
 * <p>
 * <p>
 * Author zhushuyong
 * <p>
 * Date 2017年9月7日 下午5:16:10
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping("/payment/log/payOrderLog")
public class PayOrderLogController {

    @Autowired
    private PayOrderLogService payOrderLogService;

    /**
     * Description 第三方记录列表，带分页查询
     *
     * @param pagePO
     * @return
     * @throws GlobalException
     */
    @RequestMapping(value = "/queryPayOrderLogPage", method = RequestMethod.GET)
    @ResponseBody
    public ResponseInfo queryPayOrderLogPage(PagePO pagePO, PayOrderLogEntity payOrderLogEntity)
            throws GlobalException {
        PageInfo<PayOrderLogDTO> list = payOrderLogService.queryPayOrderLogPage(pagePO, payOrderLogEntity);
        return new ResponseInfo(list);
    }

    /**
     * Description 根据id查询第三方支付记录
     */
    @RequestMapping(value = "/queryPayOrderLogView", method = RequestMethod.GET)
    @ResponseBody
    public ResponseInfo queryPayOrderLogView(Long id)
            throws GlobalException {
        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(PayOrderLogExceptionEnum.PAORDERLOG_ID_NULL);
        }
        PayOrderLogDTO payOrderLogEntity = payOrderLogService.queryPayOrderLogView(id);
        return new ResponseInfo(payOrderLogEntity);
    }

}
