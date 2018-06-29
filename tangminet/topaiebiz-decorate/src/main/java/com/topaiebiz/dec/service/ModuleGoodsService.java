package com.topaiebiz.dec.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.dec.dto.ModuleGoodsDto;
import com.topaiebiz.dec.dto.ModuleItemDto;
import com.topaiebiz.dec.entity.ModuleGoodsEntity;

import java.util.List;

/**
 * <p>
 * 模块商品详情表 服务类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
public interface ModuleGoodsService extends IService<ModuleGoodsEntity> {

    List<ModuleItemDto> getModuleItemDtos(List<Long> moduleIds);

    void saveModuleGoods(ModuleGoodsDto moduleGoodsDto);

    void modifyModuleGoods(ModuleGoodsDto moduleGoodsDto);

    void deleteModuleGoods(Long id);

    ModuleItemDto getModuleItemDto(Long moduleId);
}
