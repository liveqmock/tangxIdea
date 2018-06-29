package com.topaiebiz.transaction.payment.log.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.transaction.payment.log.dao.PayOrderLogDao;
import com.topaiebiz.transaction.payment.log.dto.PayOrderLogDTO;
import com.topaiebiz.transaction.payment.log.entity.PayOrderLogEntity;
import com.topaiebiz.transaction.payment.log.exception.PayOrderLogExceptionEnum;
import com.topaiebiz.transaction.payment.log.service.PayOrderLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description 第三方支付接口的实现层
 * <p>
 * <p>
 * Author zhushuyong
 * <p>
 * Date 2017年9月7日 下午5:07:16
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class PayOrderLogServiceImpl implements PayOrderLogService {

    @Autowired
    private PayOrderLogDao payOrderLogDao;

    @Override
    public PageInfo<PayOrderLogDTO> queryPayOrderLogPage(PagePO pagePO, PayOrderLogEntity payOrderLog) {
        Page<PayOrderLogEntity> page = PageDataUtil.buildPageParam(pagePO);
        List<PayOrderLogEntity> entities = payOrderLogDao.selectPayOrderLogPage(page, payOrderLog);
        page.setRecords(entities);
        return PageDataUtil.copyPageInfo(page, PayOrderLogDTO.class);
    }


    @Override
    public PayOrderLogDTO queryPayOrderLogView(Long id) {
        PayOrderLogEntity entity = payOrderLogDao.selectById(id);
        if (entity == null) {
            throw new GlobalException(PayOrderLogExceptionEnum.PAORDERLOG_ID_NOT_EXIST);
        }
        PayOrderLogDTO dto = new PayOrderLogDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

}
