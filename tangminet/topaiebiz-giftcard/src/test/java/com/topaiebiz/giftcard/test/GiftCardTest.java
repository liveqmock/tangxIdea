package com.topaiebiz.giftcard.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.dto.PayCard;
import com.topaiebiz.card.dto.PayInfoDTO;
import com.topaiebiz.card.dto.PaySubOrder;
import com.topaiebiz.card.dto.RefundOrderDTO;
import com.topaiebiz.giftcard.dao.GiftcardLabelDao;
import com.topaiebiz.giftcard.dao.GiftcardUnitDao;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.entity.GiftcardLabel;
import com.topaiebiz.giftcard.entity.GiftcardOrder;
import com.topaiebiz.giftcard.service.GiftcardBatchService;
import com.topaiebiz.giftcard.service.GiftcardLabelService;
import com.topaiebiz.giftcard.service.GiftcardOrderService;
import com.topaiebiz.giftcard.service.GiftcardUnitService;
import com.topaiebiz.giftcard.util.BizSerialUtil;
import com.topaiebiz.giftcard.vo.GiftcardIssueReq;
import com.topaiebiz.giftcard.vo.GiftcardIssueVO;
import com.topaiebiz.giftcard.vo.GiftcardUnitReq;
import com.topaiebiz.giftcard.vo.PlaceOrderVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午7:59 2017/12/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GiftCardTest {
    private static Logger logger = LoggerFactory.getLogger(GiftCardTest.class);

    @Autowired
    private GiftcardLabelDao giftcardLabelDao;
    @Autowired
    private GiftcardUnitDao giftcardEntityDao;

    @Autowired
    private GiftcardLabelService giftcardLabelService;

    @Autowired
    private GiftcardBatchService giftcardIssueService;
    @Autowired
    private GiftcardUnitService giftcardEntityService;

    @Autowired
    private GiftcardOrderService giftcardOrderService;

    @Autowired
    private GiftCardApi giftCardApi;

    @Test
    public void daoTest() {
        GiftcardLabel giftcardLabel = new GiftcardLabel();
        logger.info("=====test====={}===",giftcardLabelDao.insert(giftcardLabel));
    }

    @Test
    public void batchTest() {
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);

    }

    @Test
    public void queryTest() {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(3);
        pagePO.setPageSize(3);
        GiftcardIssueReq giftcardIssueReq = new GiftcardIssueReq();
        giftcardIssueReq.setCardName("美礼卡");
        giftcardIssueReq.setIssueStatus(0);
        giftcardIssueReq.setStartTime("2018-01-25");
        giftcardIssueService.queryGiftcardIssue(giftcardIssueReq);
    }

    @Test
    public void testJson() {
        logger.info("++++++paramJson+++++:{}",JSONObject.toJSONString(new GiftcardIssueVO(), SerializerFeature.WriteMapNullValue));
    }

    @Test
    public void testBatchIn() {

        GiftcardBatch giftcardIssue = giftcardIssueService.selectById(6);

        logger.info("==status={}==medium={}===vd={}",giftcardIssue.getIssueStatus(),giftcardIssue.getMedium(),giftcardIssue.getValidDays());

    }

    @Test
    public void testQueryGE() {
        GiftcardUnitReq giftcardEntityReq = new GiftcardUnitReq();
        giftcardEntityReq.setMedium(0);

        logger.info(JSONObject.toJSONString(giftcardEntityService.queryGiftcard(giftcardEntityReq)));

    }

    @Test
    public void orderTest() {
        PlaceOrderVO placeOrderVO = new PlaceOrderVO();
        placeOrderVO.setBatchId(63L);
        placeOrderVO.setIssueNum(12);
        placeOrderVO.setOrderKey(BizSerialUtil.getUUID());
        placeOrderVO.setMemberId(99999L);
        placeOrderVO.setMemberName("Jeff-2");
        placeOrderVO.setMemberPhone("18668178305");
        logger.info(">>>>>>>{}",JSONObject.toJSON(giftcardOrderService.placeOrder(placeOrderVO)));
    }

    @Test
    public void afterPayTest() {
        GiftcardOrder giftcardOrder = new GiftcardOrder();
        giftcardOrder.setId(957059665977704449L);
        giftcardOrder.setBatchId(63L);
        giftcardOrder.setPayCode("wxpay");
        giftcardOrder.setPaySn(BizSerialUtil.getUUID());
        giftcardOrder.setPayAmount(BigDecimal.valueOf(1188));
        logger.info(">>>>>>{}", JSONObject.toJSON(giftcardOrderService.updateOrderAfterPay(giftcardOrder)));
    }

    @Test
    public void cardAPITest() {
       // logger.info("1>>>>>>{}",JSONObject.toJSON(giftCardApi.getBalanceByMember(951392514894483457L)));
        //logger.info("2>>>>>>{}",JSONObject.toJSON(giftCardApi.getMemberValidCards(951392514894483457L)));
        PayInfoDTO payInfoDTO = new PayInfoDTO();
        payInfoDTO.setMemberId(951392514894483457L);
        payInfoDTO.setMemberName("Jeff");
        payInfoDTO.setMemberPhone("18668178305");
        payInfoDTO.setPaySn("paysn-kkodkeorrrdke");
        payInfoDTO.setTotalAmount(BigDecimal.valueOf(110));
        List<PaySubOrder> subOrderList = new ArrayList<>(2);
        PaySubOrder paySubOrder = new PaySubOrder();
        paySubOrder.setAmount(BigDecimal.valueOf(44));
        paySubOrder.setOrderSn("ordersn-kfoakrrefo");
        paySubOrder.setStoreId(198L);
        paySubOrder.setStoreName("母音一号店");
        List<PayCard> cardList = new ArrayList<>();
        PayCard payCard = new PayCard();
        payCard.setAmount(BigDecimal.valueOf(22));
        payCard.setCardNo("XLK8151738216871008");
        payCard.setGoodsId(88888L);
        payCard.setGoodsName("爱家奶粉3段");
        cardList.add(payCard);
        PayCard payCard1 = new PayCard();
        payCard1.setAmount(BigDecimal.valueOf(22));
        payCard1.setCardNo("XLK8151738216873742");
        payCard1.setGoodsId(66666L);
        payCard1.setGoodsName("爱家奶粉2段");
        cardList.add(payCard1);
        paySubOrder.setCardList(cardList);
        subOrderList.add(paySubOrder);

        PaySubOrder paySubOrder1 = new PaySubOrder();
        paySubOrder1.setAmount(BigDecimal.valueOf(66));
        paySubOrder1.setOrderSn("ordersn-kfeoakefod");
        paySubOrder1.setStoreId(199L);
        paySubOrder1.setStoreName("母音二号店");
        List<PayCard> cardList1 = new ArrayList<>();
        PayCard payCard11 = new PayCard();
        payCard11.setAmount(BigDecimal.valueOf(44));
        payCard11.setCardNo("XLK8151738216871008");
        payCard11.setGoodsId(55555L);
        payCard11.setGoodsName("尿不湿xl");
        cardList1.add(payCard11);
        PayCard payCard12 = new PayCard();
        payCard12.setAmount(BigDecimal.valueOf(22));
        payCard12.setCardNo("XLK8151738216873742");
        payCard12.setGoodsId(44444L);
        payCard12.setGoodsName("尿不湿sl");
        cardList1.add(payCard12);
        paySubOrder1.setCardList(cardList1);
        subOrderList.add(paySubOrder1);

        payInfoDTO.setSubOrderList(subOrderList);

       logger.info(">>>>>{}", JSONObject.toJSON(giftCardApi.payByCards(payInfoDTO)));
    }

    @Test
    public void testRefund() {
        RefundOrderDTO refundOrderDTO = new RefundOrderDTO();
        refundOrderDTO.setMemberId(951392514894483457L);
        refundOrderDTO.setMemberName("Jeff");
        refundOrderDTO.setMemberPhone("1688888888");
        refundOrderDTO.setOrderNo("ordersn-fjaefafj");
        refundOrderDTO.setTotalAmount(BigDecimal.valueOf(66));
        refundOrderDTO.setPaySn("paysn-kakfeofkoap");
        List<PayCard> payCards = new ArrayList<>();
        PayCard payCard11 = new PayCard();
        payCard11.setAmount(BigDecimal.valueOf(44));
        payCard11.setCardNo("XB18151720755713476");
        payCard11.setGoodsId(55555L);
        payCard11.setGoodsName("尿不湿xl");
        payCard11.setStoreId(199L);
        payCard11.setStoreName("母音二号店");
        payCards.add(payCard11);
        PayCard payCard12 = new PayCard();
        payCard12.setAmount(BigDecimal.valueOf(22));
        payCard12.setCardNo("XB18151720755713476");
        payCard12.setGoodsId(44444L);
        payCard12.setGoodsName("尿不湿sl");
        payCard12.setStoreId(199L);
        payCard12.setStoreName("母音二号店");
        payCards.add(payCard12);

        refundOrderDTO.setPayCardList(payCards);
        logger.info(">>>>>{}",JSONObject.toJSON(giftCardApi.refundCards(refundOrderDTO)));
    }
}
