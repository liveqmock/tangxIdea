package com.topaiebiz.merchant.freight.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.merchant.freight.dto.AddFreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.FreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.MerFreightTempleteDto;

import java.util.List;

public interface MerchantFreightService {

    //运费模版分页
    PageInfo<MerFreightTempleteDto> getMerFreightTempleteList(MerFreightTempleteDto merFreightTempleteDto);

    //添加运费模版
    void saveMerFreightTemplete(AddFreightTempleteDto addFreightTempleteDto);

    //删除运费模版
    void removeMerFreightTempleteById(Long id);

    //修改回显详情
    AddFreightTempleteDto selectMerFreightTempleteById(Long id);

    //修改运费模版
    void updateMerFreightTempleteById(AddFreightTempleteDto addFreightTempleteDto);

    //运费模版下拉框
    List<FreightTempleteDto> getList();
}
