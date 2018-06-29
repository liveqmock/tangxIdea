package com.topaiebiz.giftcard.controller.mis;

import com.alibaba.fastjson.JSONObject;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.entity.GiftcardOrder;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.service.GiftcardOrderService;
import com.topaiebiz.giftcard.util.DateUtil;
import com.topaiebiz.giftcard.util.ExportUtil;
import com.topaiebiz.giftcard.vo.GiftcardOrderReq;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 卡订单控制器
 * @author: Jeff Chen
 * @date: created in 下午2:50 2018/1/18
 */
@RestController
@RequestMapping("/giftcard/order")
public class GiftcardOrderController extends AbstractController{

    @Autowired
    private GiftcardOrderService giftcardOrderService;


    /**
     * 订单列表
     * @param giftcardOrderReq
     * @return
     */
    @RequestMapping("/query")
    @PermissionController(value = PermitType.PLATFORM,operationName = "订单列表")
    public ResponseInfo query(@RequestBody GiftcardOrderReq giftcardOrderReq) {
        return new ResponseInfo(giftcardOrderService.queryOrders(giftcardOrderReq));
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @RequestMapping("/detail/{orderId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "订单详情")
    public ResponseInfo detail(@PathVariable Long orderId) {
        return new ResponseInfo(giftcardOrderService.getByOrderId(orderId));
    }

    @RequestMapping("/export")
    @PermissionController(value = PermitType.PLATFORM,operationName = "导出订单")
    public ResponseInfo export(@RequestBody GiftcardOrderReq giftcardOrderReq, HttpServletResponse response) {
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        logger.info("{} 尝试导出 {} 订单数据", currentUserDto.getUsername(), JSONObject.toJSON(giftcardOrderReq));
        if (null == giftcardOrderReq.getStart() || null == giftcardOrderReq.getEnd() ||
                (giftcardOrderReq.getEnd() - giftcardOrderReq.getStart() > 1999)) {
            throw new GlobalException(GiftcardExceptionEnum.ONLY_IN_2000);
        }
        //订单号，礼卡编号，面值，售价，数量，优惠金额，实付，下单时间，支付时间，支付方式，会员名称，会员电话
        List<GiftcardOrder> giftcardOrders = giftcardOrderService.queryOrdersForExport(giftcardOrderReq);
        if (CollectionUtils.isEmpty(giftcardOrders)) {
            throw new GlobalException(GiftcardExceptionEnum.NO_ORDER_DATA);
        }
        List<Map<String, Object>> dataList = new ArrayList<>(giftcardOrders.size());
        giftcardOrders.forEach(giftcardOrder -> {
            Map<String, Object> data = new HashMap<>(2);
            data.put("orderId", giftcardOrder.getId()+"\t");
            data.put("batchId", giftcardOrder.getBatchId()+"\t");
            data.put("faceValue", giftcardOrder.getFaceValue());
            data.put("salePrice", giftcardOrder.getSalePrice());
            data.put("cardNum", giftcardOrder.getCardNum());
            data.put("discount", 0);
            data.put("payAmount", giftcardOrder.getPayAmount());
            data.put("orderTime", DateUtil.someDay(giftcardOrder.getCreatedTime(), 0));
            data.put("payTime", DateUtil.someDay(giftcardOrder.getPayTime(),0));
            data.put("payCode", giftcardOrder.getPayCode()==null?"":giftcardOrder.getPayCode());
            data.put("member", giftcardOrder.getMemberName());
            data.put("phone", giftcardOrder.getMemberPhone()+"\t");
            dataList.add(data);
        });
        try {
            ExportUtil.setRespProperties("礼卡订单"+giftcardOrderReq.getStart()+"~"+giftcardOrderReq.getEnd(), response);
            ExportUtil.doExport(dataList, "订单号,批次ID,面值,售价,数量,优惠,实付,下单时间,支付时间,支付方式,会员,电话",
                    "orderId,batchId,faceValue,salePrice,cardNum,discount,payAmount,orderTime,payTime,payCode,member,phone"
                    , response.getOutputStream());
        } catch (Exception e) {
            logger.error("导出订单错误",e);
        }
        return null;
    }
}
