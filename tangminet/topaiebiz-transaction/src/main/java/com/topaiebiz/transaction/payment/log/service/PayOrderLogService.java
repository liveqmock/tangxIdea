package com.topaiebiz.transaction.payment.log.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.transaction.payment.log.dto.PayOrderLogDTO;
import com.topaiebiz.transaction.payment.log.entity.PayOrderLogEntity;

/**
 * Description 订单三方支付记录接口
 * <p>
 * Author zhushuyong
 * <p>
 * Date 2017年9月7日 下午4:58:16
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface PayOrderLogService {

    /**
     * Description 第三方记录列表，带分页查询
     * <p>
     * Author zhushuyong
     *
     * @param pagePO
     * @return
     */
    PageInfo<PayOrderLogDTO> queryPayOrderLogPage(PagePO pagePO, PayOrderLogEntity payOrderLog);

    /**
     * Description 根据id查询第三方支付记录
     * <p>
     * Author zhushuyong
     *
     * @param id
     * @return
     */
    PayOrderLogDTO queryPayOrderLogView(Long id);

}
