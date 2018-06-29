package com.topaiebiz.goods.favorite.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.api.GoodsFavoriteApi;
import com.topaiebiz.goods.favorite.dto.GoodsFavoriteDto;
import com.topaiebiz.goods.favorite.service.GoodsFavoriteService;
import com.topaiebiz.goods.sku.dto.ItemDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description 收藏夹的控制层
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年9月8日 下午4:11:20
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@MemberLogin
@RestController
@RequestMapping(value = "/goods/favorite",method = RequestMethod.POST)
public class GoodsFavoriteController {

    @Autowired
    private GoodsFavoriteService favoriteService;

    @Autowired
    private GoodsFavoriteApi goodsFavoriteApi;

    /**
     * Description app端收藏夹列表
     * <p>
     * Author Hedda
     * @param pagePO 分页单位
     * @return
     */
    @RequestMapping(value = "/getGoodsFavoriteList")
    public ResponseInfo getGoodsFavoriteList(@RequestBody PagePO pagePO) {
        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        PageInfo<GoodsFavoriteDto> goodsFavoriteList = favoriteService.getGoodsFavoriteListByMemberId(pagePO, memberId);
        return new ResponseInfo(goodsFavoriteList);
    }

    /**
     * Description app端删除收藏夹
     * <p>
     * Author Hedda
     *
     * @param id 收藏夹的id
     * @return
     */
    @RequestMapping(value = "/cancelGoodsFavorite/{id}")
    public ResponseInfo cancelGoodsFavorite(@PathVariable Long id) {
        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        return new ResponseInfo(favoriteService.removelGoodsFavorite(id,memberId));
    }

    /**
     * Description app端将商品添加到收藏夹
     * <p>
     * Author Hedda
     *
     * @param itemIds 商品itemIds
     * @return
     */
    @RequestMapping(value = "/saveGoodsFavorite")
    public ResponseInfo saveGoodsFavorite(@RequestBody List<Long> itemIds) {
        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        return new ResponseInfo(goodsFavoriteApi.addFavorite(memberId,itemIds));
    }

    /**
     * Description 查询此商品是否被选为收藏
     * <p>
     * Author Hedda
     *
     * @param itemId 商品id
     * @return
     */
    @RequestMapping(value = "/findGoodsFavorite/{itemId}")
    public ResponseInfo findGoodsFavorite(@PathVariable Long itemId) {
        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        return new ResponseInfo(favoriteService.findGoodsFavorite(memberId, itemId));
    }
}