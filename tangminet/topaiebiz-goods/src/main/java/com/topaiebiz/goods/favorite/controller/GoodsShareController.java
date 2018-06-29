package com.topaiebiz.goods.favorite.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.favorite.service.GoodsShareService;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by dell on 2018/1/5.
 */
@MemberLogin
@RestController
@RequestMapping(value = "/goods/share",method = RequestMethod.POST)
public class GoodsShareController {

    @Autowired
    private GoodsShareService goodsShareService;

    /**
     * Description app端将商品分享
     *
     * Author Hedda
     *
     * @param itemIds 商品item的id
     * @return
     * @throws GlobalException
     */
    @RequestMapping(value = "/saveGoodsSharing")
    public ResponseInfo saveGoodssharing(@RequestBody Long[] itemIds) throws GlobalException {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        return new ResponseInfo(goodsShareService.saveGoodsSharing(memberId, itemIds));
    }
}
