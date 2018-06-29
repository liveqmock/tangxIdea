package com.topaiebiz.settlement.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.settlement.entity.StoreSettlementRefundOrderEntity;
import com.topaiebiz.settlement.service.SettlementJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-03-28 9:17
 */
@Slf4j
@RestController
@RequestMapping(value = "/settlement/task/", method = RequestMethod.POST)
public class SettlementTaskController {

    @Autowired
    private StoreApi storeApi;
    @Autowired
    private SettlementJobService settlementJobService;

    @RequestMapping(path = "/action")
    public ResponseInfo getSettlementList(@RequestParam("storeId") Long storeId,
                                          @RequestParam("start") String startVal,
                                          @RequestParam("end") String endVal) {
        StoreInfoDetailDTO storeDTO = storeApi.getStore(storeId);
        if (storeDTO == null) {
            log.error("店铺不存在");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date start = sdf.parse(startVal);
                Date end = sdf.parse(endVal);
                storeDTO.setId(storeId);
                settlementJobService.createSettlement(storeDTO, start, end);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return new ResponseInfo();
    }

    /**
     * 获取需要订正退款订单佣金的结算单号集合
     *
     * @return
     */
    @RequestMapping(path = "/getSettleIds")
    public ResponseInfo getSettleIds() {
        return new ResponseInfo(settlementJobService.getSettleIds());
    }

    /**
     * 重置需要订正退款订单佣金的结算单
     *
     * @param ids
     * @return
     */
    @RequestMapping(path = "/putRefundCommission")
    public ResponseInfo putRefundCommission(@RequestBody List<Long> ids) {
        return new ResponseInfo(settlementJobService.putRefundCommission(ids));
    }

    /**
     * 获取需要订正商品详情的订单号集合
     *
     * @return
     */
    @RequestMapping(path = "/getOrderIds")
    public ResponseInfo getOrderIds() {
        return new ResponseInfo(settlementJobService.getOrderIds());
    }

    /**
     * 重置需要订正商品详情的结算订单
     *
     * @param orderIds
     * @return
     */
    @RequestMapping(path = "/putOrderGoodDetail")
    public ResponseInfo putOrderGoodDetail(@RequestBody List<Long> orderIds) {
        return new ResponseInfo(settlementJobService.putOrderGoodDetail(orderIds));
    }

    /**
     * 获取需要订正商品详情的售后订单号集合
     *
     * @return
     */
    @RequestMapping(path = "/getRefundIds")
    public ResponseInfo getRefundIds() {
        return new ResponseInfo(settlementJobService.getRefundIds());
    }

    /**
     * 重置需要订正商品详情的售后订单
     *
     * @param refundList
     * @return
     */
    @RequestMapping(path = "/putRefundGoodDetail")
    public ResponseInfo putRefundGoodDetail(@RequestBody List<StoreSettlementRefundOrderEntity> refundList) {
        return new ResponseInfo(settlementJobService.putRefundGoodDetail(refundList));
    }
}
