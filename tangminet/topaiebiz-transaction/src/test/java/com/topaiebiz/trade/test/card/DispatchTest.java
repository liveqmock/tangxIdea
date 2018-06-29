package com.topaiebiz.trade.test.card;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.card.dto.BriefCardDTO;
import com.topaiebiz.trade.order.core.pay.bo.GoodsPayBO;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import com.topaiebiz.trade.order.core.pay.context.PayConfigContext;
import com.topaiebiz.trade.order.core.pay.util.BalanceDispatcher;
import com.topaiebiz.trade.order.core.pay.util.CardDispatcher;
import com.topaiebiz.trade.order.core.pay.util.ScoreDispatcher;
import com.topaiebiz.trade.order.dto.pay.PayConfiguration;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-03-16 12:40
 */
public class DispatchTest {
    private ScoreDispatcher scoreDispatcher = new ScoreDispatcher();
    private BalanceDispatcher balanceDispatcher = new BalanceDispatcher();

    @Test
    public void amountTest() {
        PayConfiguration payConf = new PayConfiguration();
        payConf.setCardStoreBlackList(new HashSet<>());
        PayConfigContext.set(payConf);

        //创建可用礼卡
        List<BriefCardDTO> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BriefCardDTO card = new BriefCardDTO();
            card.setCardNo("card-no-" + i);
            card.setPriority(1);
            card.setIssuedTime(10000);
            card.setExpiredTime(0);
            card.setApplyScope(ApplyScopeEnum.APPLY_ALL.getScopeId());
            card.setAmount(new BigDecimal(20).add(new BigDecimal(i)));

            cards.add(card);
        }

        //创建待支付订单
        List<StorePayBO> storePayDetails = new ArrayList<>();

        StorePayBO storePayBO = new StorePayBO();
        storePayBO.setFreightPrice(new BigDecimal("5"));
        storePayBO.setOrderId(1L);
        storePayBO.setStoreId(111L);
        storePayBO.setStoreName("测试店铺");
        storePayBO.setTotalPrice(new BigDecimal(106));
        storePayBO.setGoodsPrice(new BigDecimal(100));
        storePayBO.setFreightPrice(new BigDecimal(6));
        storePayDetails.add(storePayBO);

        List<GoodsPayBO> goodsPayDetails = new ArrayList<>();
        GoodsPayBO goodsPayBO1 = new GoodsPayBO();
        goodsPayBO1.setDetailId(1001L);
        goodsPayBO1.setGoodsName("测试商品1");
        goodsPayBO1.setScoreRate(new BigDecimal("90"));
        goodsPayBO1.setSkuId(11111111L);
        goodsPayBO1.setPayPrice(new BigDecimal(60));

        GoodsPayBO goodsPayBO2 = new GoodsPayBO();
        goodsPayBO2.setDetailId(1002L);
        goodsPayBO2.setGoodsName("测试商品2");
        goodsPayBO2.setScoreRate(new BigDecimal("80"));
        goodsPayBO2.setSkuId(11111112L);
        goodsPayBO2.setPayPrice(new BigDecimal(40));

        goodsPayDetails.add(goodsPayBO1);
        goodsPayDetails.add(goodsPayBO2);
        storePayBO.setGoodsPayDetails(goodsPayDetails);

        balanceDispatcher.dispatch(new BigDecimal(10), storePayDetails);
        System.out.println(JSON.toJSONString(storePayDetails, true));
        
        CardDispatcher.dispatch(new BigDecimal(6), cards, storePayDetails);
        System.out.println(JSON.toJSONString(storePayDetails, true));

        scoreDispatcher.dispatch(new BigDecimal(100), storePayDetails);
        System.out.println(JSON.toJSONString(storePayDetails, true));
    }
}