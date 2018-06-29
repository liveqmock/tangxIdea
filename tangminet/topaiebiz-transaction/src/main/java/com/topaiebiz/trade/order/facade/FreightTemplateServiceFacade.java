package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.merchant.api.FreightTemplateApi;
import com.topaiebiz.merchant.dto.template.FreightTemplateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-15 19:05
 */
@Component
@Slf4j
public class FreightTemplateServiceFacade {

    @Autowired
    private FreightTemplateApi freightTemplateApi;

    /**
     * 批量查询运费模板
     *
     * @param freightIds
     * @return
     */
    public Map<Long, FreightTemplateDTO> getFreightTemplete(List<Long> freightIds) {
        Map<Long, FreightTemplateDTO> freightDatas = freightTemplateApi.getFreightTemplete(freightIds);
        log.info("freightTemplateApi.getFreightTemplete({}) return:{}", JSON.toJSONString(freightIds), JSON.toJSONString(freightDatas));
        return freightDatas;
    }
}