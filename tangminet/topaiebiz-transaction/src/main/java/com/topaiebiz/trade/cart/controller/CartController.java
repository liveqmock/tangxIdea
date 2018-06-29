package com.topaiebiz.trade.cart.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.nebulapaas.common.BindResultUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.trade.cart.po.CartAddPO;
import com.topaiebiz.trade.cart.po.CartEditPO;
import com.topaiebiz.trade.cart.service.ShoppingCartService;
import com.topaiebiz.trade.order.exception.ShoppingCartExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/***
 * 购物车controller
 * @author yfeng
 * @date 2017-12-20 9:56
 */
@MemberLogin
@RestController
@Slf4j
@RequestMapping(value = "/trade/cart/",method = RequestMethod.POST)
public class CartController {

    @Autowired
    private ShoppingCartService cartService;

    @RequestMapping(value = "/getCarts")
    public ResponseInfo getCarts() {
        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();

        return new ResponseInfo(cartService.query(memberId));
    }

    @RequestMapping(value = "/addCart")
    public ResponseInfo add(@RequestBody @Valid CartAddPO cart, BindingResult result) {
        log.info("add : {}", JSON.toJSONString(cart));

        BindResultUtil.dealBindResult(result);

        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();

        Boolean addResult = cartService.addCart(memberId, cart);
        return new ResponseInfo(addResult);
    }

    @RequestMapping(value = "/removeCart/{cartId}")
    public ResponseInfo removeCart(@PathVariable Long cartId) {
        log.info(">>>>> removeCart: {}", cartId);

        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();

        List<Long> cartIds = Lists.newArrayList(cartId);
        return new ResponseInfo(cartService.removeCarts(memberId, cartIds));
    }

    @RequestMapping(value = "/removeCarts")
    public ResponseInfo removeCarts(@RequestBody List<Long> cartIds) {
        log.info(">>>>> removeCarts: {}", JSON.toJSONString(cartIds));
        if (CollectionUtils.isEmpty(cartIds)) {
            log.warn("do removeCarts but input cartIds is empty");
            throw new GlobalException(ShoppingCartExceptionEnum.GOODSCART_NOT_EXIST);
        }
        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();

        return new ResponseInfo(cartService.removeCarts(memberId, cartIds));
    }

    @RequestMapping(value = "/editCart")
    public ResponseInfo editCart(@RequestBody @Valid CartEditPO editPO, BindingResult result) {
        log.info(">>>>> editPO: {}", JSON.toJSONString(editPO));

        BindResultUtil.dealBindResult(result);

        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();

        return new ResponseInfo(cartService.editCart(memberId, editPO));
    }

    @RequestMapping(value = "/editCarts")
    public ResponseInfo editCart(@RequestBody @Valid List<CartEditPO> editPOs, BindingResult result) {
        BindResultUtil.dealBindResult(result);
        log.info(">>>>> editCart: {}", JSON.toJSONString(editPOs));
        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();

        return new ResponseInfo(cartService.editCarts(memberId, editPOs));
    }

    @RequestMapping(value = "/moveToFavorite/{cartId}")
    public ResponseInfo moveToFavorite(@PathVariable Long cartId) {
        //获取登录用户ID
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        return new ResponseInfo(cartService.moveToFavorite(memberId, cartId));
    }
}