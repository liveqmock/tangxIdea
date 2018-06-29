package com.topaiebiz.transport.expressage.api.impl;

import com.topaiebiz.transport.api.ExpressageApi;
import com.topaiebiz.transport.dto.ExpressageParamDto;
import com.topaiebiz.transport.dto.LogisticsDto;
import com.topaiebiz.transport.expressage.service.ExpressageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpressageApiImpl implements ExpressageApi {

    @Autowired
    private ExpressageService expressageService;

    @Override
    public boolean sendExpress(ExpressageParamDto expressageParamDto) {
         return expressageService.subscriptionExpressage(expressageParamDto);
    }

    @Override
    public LogisticsDto getLogistics(Long id) {
        return expressageService.getLogistics(id);
    }

    @Override
    public LogisticsDto getLogisticsByCode(String expressCompanyCode) {
        return expressageService.getLogisticsByCode(expressCompanyCode);
    }
}
