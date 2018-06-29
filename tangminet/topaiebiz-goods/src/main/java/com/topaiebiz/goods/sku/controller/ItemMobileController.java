package com.topaiebiz.goods.sku.controller;


import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.sku.dto.StoreCouponDTO;
import com.topaiebiz.goods.sku.dto.app.GoodsPraiseDto;
import com.topaiebiz.goods.sku.dto.app.GoodsSkuCommentAppDto;
import com.topaiebiz.goods.sku.dto.app.ItemAppDto;
import com.topaiebiz.goods.sku.dto.app.ItemCustomerDto;
import com.topaiebiz.goods.sku.service.ItemService;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description app端商品sku控制层
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年10月3日 下午2:35:46
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/goods/item", method = RequestMethod.POST)
public class ItemMobileController {

    @Autowired
    private ItemService itemService;

    /**
     * Description app端根据年龄段,品牌，分类商家查询商品
     * <p>
     * Author Hedda
     *
     * @return
     */
    @RequestMapping(path = "/getGoodsList")
    public ResponseInfo getGoodsList(@RequestBody ItemCustomerDto itemCustomerDto) throws GlobalException {
        PageInfo<ItemCustomerDto> itemCustomerDtos = itemService.getGoodsList(itemCustomerDto);
        return new ResponseInfo(itemCustomerDtos);
    }

    /**
     * Description app根据商品id查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @MemberLogin
    @RequestMapping(path = "/findGoodsById/{id}")
    public ResponseInfo findAppItemById(@PathVariable Long id) throws GlobalException {
        ItemAppDto itemDto = itemService.findAppItemById(id);
        return new ResponseInfo(itemDto);
    }

    /**
     * Description 根据商品id查询对应商品评价
     * <p>
     * Author Hedda
     *
     * @param goodsSkuCommentAppDto 商品id
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/findAppSkuCommentById")
    public ResponseInfo findAppSkuCommentById(@RequestBody GoodsSkuCommentAppDto goodsSkuCommentAppDto) throws GlobalException {
        PageInfo<GoodsSkuCommentDto> goodsSkuCommentDtos = itemService.findAppSkuCommentById(goodsSkuCommentAppDto);
        return new ResponseInfo(goodsSkuCommentDtos);
    }

    /**
     * Description 根据商品id查询对应评价等级与条数
     * <p>
     * Author Hedda
     * @param itemId 商品id
     * @return
     */
    @RequestMapping(path = "/findAppGoodsPraiseById/{itemId}")
    public ResponseInfo findAppGoodsPraiseById(@PathVariable Long itemId){
       GoodsPraiseDto goodsPraiseDto = itemService.findAppGoodsPraiseById(itemId);
        return new ResponseInfo(goodsPraiseDto);
    }

}
