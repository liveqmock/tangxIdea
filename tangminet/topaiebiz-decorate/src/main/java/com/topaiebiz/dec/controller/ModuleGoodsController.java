package com.topaiebiz.dec.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.dec.dto.ModuleGoodsDto;
import com.topaiebiz.dec.service.ModuleGoodsService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 模块商品详情表 前端控制器
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 *
 */
@RestController
@Slf4j
@RequestMapping(value = "/decorate/moduleGoods",method = RequestMethod.POST)
public class ModuleGoodsController {
	@Autowired
    ModuleGoodsService moduleGoodsService;

	@PermissionController(value= PermitType.PLATFORM,operationName = "根据模块商品详情添加模块商品")
    @RequestMapping(value = "/addModuleGoods")
    public ResponseInfo addModuleGoods(@RequestBody ModuleGoodsDto moduleGoodsDto) throws GlobalException {
        moduleGoodsService.saveModuleGoods(moduleGoodsDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块商品详情修改模块商品")
    @RequestMapping(value="/modifyModuleGoods")
    public ResponseInfo modifyModuleGoods(@RequestBody ModuleGoodsDto moduleGoodsDto) throws GlobalException {
        moduleGoodsService.modifyModuleGoods(moduleGoodsDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块商品id删除模块商品")
    @RequestMapping(value="/removeModuleGoods")
    public ResponseInfo removeModuleGoods(@RequestBody Long id) throws GlobalException{
        moduleGoodsService.deleteModuleGoods(id);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块id集合查询模块商品")
    @RequestMapping(value="/searchModuleGoods")
    public ResponseInfo searchModuleGoods(@RequestBody List<Long> moduleIds) throws GlobalException{
        return new ResponseInfo(moduleGoodsService.getModuleItemDtos(moduleIds));
    }


}
