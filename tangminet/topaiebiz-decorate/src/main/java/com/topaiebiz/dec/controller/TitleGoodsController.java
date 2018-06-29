package com.topaiebiz.dec.controller;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.dec.dto.TitleGoodsDto;
import com.topaiebiz.dec.service.TitleGoodsService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 标题商品详情表 前端控制器
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@RestController
@Slf4j
@RequestMapping(value = "/decorate/titleGoods",method = RequestMethod.POST)
public class TitleGoodsController {
    @Autowired
    TitleGoodsService titleGoodsService;

    @PermissionController(value = PermitType.PLATFORM,operationName = "添加标题商品")
    @RequestMapping(value = "/addTitleGoods")
    public ResponseInfo addTitleGoods(@RequestBody TitleGoodsDto TitleGoodsDto) throws GlobalException {
        titleGoodsService.saveTitleGoodsDto(TitleGoodsDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据标题商品id删除标题商品")
    @RequestMapping(value = "/removeTitleGoods")
    public ResponseInfo removeTitleGoods(@RequestBody Long id) throws GlobalException{
        log.info(" remove {} ",String.valueOf(id));
        titleGoodsService.deleteTitleGoodsDto(id);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据标题商品id查询标题商品")
    @RequestMapping(value = "/searchTitleGoods")
    public ResponseInfo searchTitleGoods(@RequestBody Long titleId) throws GlobalException{
        log.info(" remove {} ",titleId);
     //   return new ResponseInfo(titleGoodsService.getTitleGoodsDtos(titleId));
        return new ResponseInfo(titleGoodsService.getTitleItemDto(titleId));
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "修改标题商品")
    @RequestMapping(value = "/modifyTitleGoods")
    public ResponseInfo modifyTitleGoods(@RequestBody TitleGoodsDto titleGoodsDto)throws GlobalException{
        log.info(" modify {} ", JSON.toJSONString(titleGoodsDto));
        titleGoodsService.modifyTitleGoods(titleGoodsDto);
        return new ResponseInfo();
    }
}
