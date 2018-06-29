package com.topaiebiz.goods.favorite.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.favorite.dto.GoodsFootprintDto;
import com.topaiebiz.goods.favorite.service.GoodsFootprintService;
import com.topaiebiz.goods.sku.dto.ItemDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by dell on 2018/1/5.
 */
@MemberLogin
@RestController
@RequestMapping(value = "/goods/footprint",method = RequestMethod.POST)
public class GoodsFootprintController {

    @Autowired
    private GoodsFootprintService goodsFootprintService;

    /**
     * Description 我的足迹列表
     *
     * Author Hedda
     * @param goodsFootprintDto
     * @return
     * @throws GlobalException
     */
    @RequestMapping(value = "/getGoodsFootprintList")
    public ResponseInfo getGoodsFootprintList(@RequestBody GoodsFootprintDto goodsFootprintDto)
            throws GlobalException {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        PageInfo<GoodsFootprintDto> goodsFootprintList = goodsFootprintService.getGoodsFootprintListByMemberId(goodsFootprintDto, memberId);
        return new ResponseInfo(goodsFootprintList);
    }

    /**
     * Description 删除我的足迹
     *
     * Author Hedda
     *
     * @param id
     *            商品id
     * @return
     * @throws GlobalException
     */
    @RequestMapping(value = "/cancelGoodsFootprint")
    public ResponseInfo cancelGoodsFootprint(@RequestBody Long[] id) throws GlobalException {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        return new ResponseInfo(goodsFootprintService.removelGoodsFootprint(id));
    }

    /**
     * Description app端我的足迹添加
     *
     * Author Hedda
     *
     * @param itemIds
     *            商品id
     * @return
     * @throws GlobalException
     */
    @RequestMapping(value = "/saveGoodsFootprint")
    public ResponseInfo saveGoodsFootprint(@RequestBody Long[] itemIds) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        return new ResponseInfo(goodsFootprintService.addGoodsFootprint(memberId, itemIds));
    }

}
