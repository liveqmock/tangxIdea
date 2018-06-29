package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.transport.api.ExpressageApi;
import com.topaiebiz.transport.dto.ExpressageParamDto;
import com.topaiebiz.transport.dto.LogisticsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description 物流接口
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/30 17:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class ExpressageServiceFacade {

    @Autowired
    private ExpressageApi expressageApi;

    /**
     * Description: 发送物流信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/30
     *
     * @param:
     **/
    public boolean sendExpress(Long logisticsId, String logisticsNo) {
        ExpressageParamDto expressageParamDto = new ExpressageParamDto();
        expressageParamDto.setId(logisticsId);
        expressageParamDto.setNumber(logisticsNo);
        Boolean result = expressageApi.sendExpress(expressageParamDto);
        log.info("----------expressageApi.sendExpress-- request params:{}, result:{}", JSON.toJSONString(expressageParamDto), result);
        return result;
    }

    /**
     * Description: 获取物流公司详情-- id
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/30
     *
     * @param:
     **/
    public LogisticsDto getLogistics(Long logisticsCompanyId) {
        LogisticsDto logisticsDto = expressageApi.getLogistics(logisticsCompanyId);
        log.info("----------expressageApi.getLogistics BY ID-- request params:{}, result:{}", logisticsCompanyId, JSON.toJSONString(logisticsDto));
        if (null == logisticsDto) {
            log.error("----------submit logistics info fail, logistics company is not found!");
            throw new GlobalException(RefundOrderExceptionEnum.CANT_FOUND_THE_EXPRESS_COMPANY_IN_DATADICT);
        }
        return logisticsDto;
    }

    /**
     * Description: 获取物流公司详情-- code
     * <p>
     * Author: hxpeng
     * createTime: 2018/5/7
     *
     * @param:
     **/
    public LogisticsDto getLogistics(String expressCompanyCode) {
        LogisticsDto logisticsDto = expressageApi.getLogisticsByCode(expressCompanyCode);
        log.info("----------expressageApi.getLogistics BY CODE-- request params:{}, result:{}", expressCompanyCode, JSON.toJSONString(logisticsDto));
        if (null == logisticsDto) {
            log.error("----------submit logistics info fail, logistics company is not found!");
            throw new GlobalException(RefundOrderExceptionEnum.CANT_FOUND_THE_EXPRESS_COMPANY_IN_DATADICT);
        }
        return logisticsDto;
    }

}
