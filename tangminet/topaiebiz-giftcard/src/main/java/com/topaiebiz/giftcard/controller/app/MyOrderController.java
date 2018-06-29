package com.topaiebiz.giftcard.controller.app;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.BizConstants;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.entity.GiftcardOrder;
import com.topaiebiz.card.constant.CardOrderStatusEnum;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.service.GiftcardOrderItemService;
import com.topaiebiz.giftcard.service.GiftcardOrderService;
import com.topaiebiz.giftcard.vo.GiftcardOrderReq;
import com.topaiebiz.giftcard.vo.MyOrderReq;
import com.topaiebiz.giftcard.vo.PlaceOrderVO;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @description: 礼卡订单控制
 * @author: Jeff Chen
 * @date: created in 下午2:41 2018/1/24
 */
@RestController
@RequestMapping("/app/giftcard/order")
public class MyOrderController extends AbstractController {

    @Autowired
    private GiftcardOrderService giftcardOrderService;

    @Autowired
    private GiftcardOrderItemService giftcardOrderItemService;
    /**
     * 礼卡订单
     * @param myOrderReq
     * @return
     */
    @RequestMapping("/list")
    @MemberLogin
    public ResponseInfo list(@RequestBody MyOrderReq myOrderReq) {
        GiftcardOrderReq req = new GiftcardOrderReq();
        req.setMemberId(MemberContext.getMemberId());
        req.setOrderStatus(myOrderReq.getOrderStatus());
        return new ResponseInfo(giftcardOrderService.queryOrders(req));
    }

    /**
     * 进入下单页
     * @param placeOrderVO
     * @return
     */
    @RequestMapping("/place")
    @MemberLogin
    public ResponseInfo place(@Valid @RequestBody PlaceOrderVO placeOrderVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        if (null == placeOrderVO || null == placeOrderVO.getBatchId() || null == placeOrderVO.getIssueNum()) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_REQ);
        }
        MemberTokenDto memberTokenDto = MemberContext.getCurrentMemberToken();
        placeOrderVO.setMemberPhone(memberTokenDto.getTelephone());
        placeOrderVO.setMemberName(memberTokenDto.getUserName());
        placeOrderVO.setMemberId(memberTokenDto.getMemberId());
        placeOrderVO.setReqType(BizConstants.ORDER_REQ_FILL_IN);
        return new ResponseInfo(giftcardOrderService.prepareOrder(placeOrderVO));
    }

    /**
     * 去支付：生单，返回订单id
     * @param placeOrderVO
     * @param result
     * @return
     */
    @RequestMapping("/goPay")
    @MemberLogin
    public ResponseInfo goPay(@Valid @RequestBody PlaceOrderVO placeOrderVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        MemberTokenDto memberTokenDto = MemberContext.getCurrentMemberToken();
        placeOrderVO.setMemberPhone(memberTokenDto.getTelephone());
        placeOrderVO.setMemberName(memberTokenDto.getUserName());
        placeOrderVO.setMemberId(memberTokenDto.getMemberId());
        placeOrderVO.setReqType(BizConstants.ORDER_REQ_PLACE_ORDER);
        return new ResponseInfo(giftcardOrderService.placeOrder(placeOrderVO));
    }

    /**
     * 支付回调测试
     * @param giftcardOrder
     * @return
     */
    @MemberLogin
    public ResponseInfo payCallbackTest(@RequestBody GiftcardOrder giftcardOrder) {
        if (null == giftcardOrder || null == giftcardOrder.getId() || null == giftcardOrder.getBatchId()
                || null== giftcardOrder.getPayAmount() || null== giftcardOrder.getPayCode()
                ||null==  giftcardOrder.getPaySn()) {
            return paramError();
        }
        giftcardOrder.setMemberId(MemberContext.getMemberId());
        return new ResponseInfo(giftcardOrderService.updateOrderAfterPay(giftcardOrder));
    }

    /**
     * 删除订单
     * @param orderId
     * @return
     */
    @RequestMapping("/del/{orderId}")
    @MemberLogin
    public ResponseInfo del(@PathVariable Long orderId) {
        GiftcardOrder giftcardOrder = new GiftcardOrder();
        giftcardOrder.setMemberId(MemberContext.getMemberId());
        giftcardOrder.setId(orderId);
        giftcardOrder.setDelFlag(1);
        return new ResponseInfo(giftcardOrderService.updateOrderByMember(giftcardOrder));
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @RequestMapping("/cancel/{orderId}")
    @MemberLogin
    public ResponseInfo cancel(@PathVariable Long orderId) {
        GiftcardOrder giftcardOrder = new GiftcardOrder();
        giftcardOrder.setMemberId(MemberContext.getMemberId());
        giftcardOrder.setId(orderId);
        giftcardOrder.setOrderStatus(CardOrderStatusEnum.CANCELED.getStatusCode());
        return new ResponseInfo(giftcardOrderService.updateOrderByMember(giftcardOrder));
    }

    /**
     * 礼卡详情
     * @param orderId
     * @return
     */
    @RequestMapping("/detail/{orderId}")
    @MemberLogin
    public ResponseInfo detail(@PathVariable Long orderId) {
        return new ResponseInfo(giftcardOrderService.getMyOrderDetail(MemberContext.getMemberId(),orderId));
    }

    /**
     * 成功后的订单信息
     * @param orderId
     * @return
     */
    @RequestMapping("/succ/{orderId}")
    @MemberLogin
    public ResponseInfo succ(@PathVariable Long orderId) {
        return new ResponseInfo(giftcardOrderItemService.getPaidOrderById(orderId,MemberContext.getMemberId()));
    }

    /**
     * 获取订单金额
     * @param orderId
     * @return
     */
    @RequestMapping("/amount/{orderId}")
    @MemberLogin
    public ResponseInfo amount(@PathVariable Long orderId) {
        return new ResponseInfo(giftcardOrderService.getOrderAmount(orderId));
    }
}
