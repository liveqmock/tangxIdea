package com.topaiebiz.transport.api;

import com.topaiebiz.transport.dto.ExpressageParamDto;
import com.topaiebiz.transport.dto.LogisticsDto;

import java.util.List;

public interface ExpressageApi {

    //发送快递
    boolean sendExpress(ExpressageParamDto expressageParamDto);

    //校验物流公司是否正确
    LogisticsDto getLogistics(Long id);

    /**
    *
    * Description: 根据物流公司CODE 查询
    *
    * Author: hxpeng
    * createTime: 2018/3/6
    *
    * @param:
    **/
    LogisticsDto getLogisticsByCode(String expressCompanyCode);
}
