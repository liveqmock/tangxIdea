package com.topaiebiz.trade.order.core.pay.util;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.card.dto.BriefCardDTO;
import com.topaiebiz.trade.order.core.pay.bo.GoodsPayBO;
import com.topaiebiz.trade.order.core.pay.bo.PkgDispatchBO;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import com.topaiebiz.trade.order.core.pay.context.PayConfigContext;
import com.topaiebiz.trade.order.dto.pay.PayConfiguration;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.*;

/***
 * @author yfeng
 * @date 2018-01-19 16:16
 */
@Slf4j
public class CardDispatcher {
    /**
     * 定义礼卡范围的优先级
     */
    private static Map<Integer, Integer> scopePriorityMap = new HashMap<>();

    /**
     * 礼卡范围的优先级
     */
    private static final Integer CARD_SCOPE_HIGH = 3;
    private static final Integer CARD_SCOPE_MIDDLE = 2;
    private static final Integer CARD_SCOPE_LOW = 1;
    private static final Integer CARD_SCOPE_UNKNOWN = 0;

    static {
        //单品礼卡优先级最高
        scopePriorityMap.put(ApplyScopeEnum.APPLY_GOODS.getScopeId(), CARD_SCOPE_HIGH);

        //店铺礼卡优先级居中
        scopePriorityMap.put(ApplyScopeEnum.APPLY_EXCLUDE.getScopeId(), CARD_SCOPE_MIDDLE);
        scopePriorityMap.put(ApplyScopeEnum.APPLY_INCLUDE.getScopeId(), CARD_SCOPE_MIDDLE);

        //平台礼卡优先级最低
        scopePriorityMap.put(ApplyScopeEnum.APPLY_ALL.getScopeId(), CARD_SCOPE_LOW);
    }

    private static Integer getCardScopePriority(BriefCardDTO card) {
        if (card == null || !scopePriorityMap.containsKey(card.getApplyScope())) {
            return CARD_SCOPE_UNKNOWN;
        }
        return scopePriorityMap.get(card.getApplyScope());
    }

    private static Comparator<BriefCardDTO> cardComparator = new Comparator<BriefCardDTO>() {
        private int BEFORE = -1;
        private int SAME = 0;
        private int AFTER = 1;

        @Override
        public int compare(BriefCardDTO card1, BriefCardDTO card2) {
            //按照权重高优先的规则排序
            if (card1.getPriority() > card2.getPriority()) {
                return BEFORE;
            } else if (card1.getPriority() < card2.getPriority()) {
                return AFTER;
            }

            //按照礼卡使用范围的规则排序
            Integer card1ScopePriority = getCardScopePriority(card1);
            Integer card2ScopePriority = getCardScopePriority(card2);
            if (card1ScopePriority > card2ScopePriority) {
                return BEFORE;
            } else if (card1ScopePriority < card2ScopePriority) {
                return AFTER;
            }

            //按照余额低优先的规则排序
            if (MathUtil.less(card1.getAmount(), card2.getAmount())) {
                return BEFORE;
            } else if (MathUtil.greator(card1.getAmount(), card2.getAmount())) {
                return AFTER;
            }

            //按照卡到期时间近优先的规则排序
            if (card1.getExpiredTime() < card2.getExpiredTime()) {
                return BEFORE;
            } else if (card1.getExpiredTime() > card2.getExpiredTime()) {
                return AFTER;
            }

            //按照发卡时间早优先的规则排序
            if (card1.getIssuedTime() < card2.getIssuedTime()) {
                return BEFORE;
            } else if (card1.getIssuedTime() > card2.getIssuedTime()) {
                return AFTER;
            }
            return SAME;
        }
    };

    private static Comparator<PkgDispatchBO> pkgDispatchComparator = new Comparator<PkgDispatchBO>() {

        @Override
        public int compare(PkgDispatchBO o1, PkgDispatchBO o2) {
            Integer cardMatchCount1 = o1 == null ? 0 : o1.getMatchCardCount();
            Integer cardMatchCount2 = o2 == null ? 0 : o2.getMatchCardCount();
            //按照店铺订单支持礼卡数量升序排序
            return cardMatchCount1.compareTo(cardMatchCount2);
        }
    };

    private static void updateCard(BriefCardDTO card, int priority, int amount, Integer expire, Integer create) {
        card.setPriority(priority);
        card.setAmount(new BigDecimal(amount));
        card.setExpiredTime(expire);
        card.setIssuedTime(create);
    }

    /**
     * 礼卡分摊
     *
     * @param dispatchLimit   分摊金额限制，支付提交或加载时计算订单可分摊总额
     * @param cards           用户礼卡信息
     * @param storePayDetails
     * @return 参与分摊的商品集合
     */
    public static Pair<List<BriefCardDTO>, BigDecimal> dispatch(BigDecimal dispatchLimit, List<BriefCardDTO> cards, List<StorePayBO> storePayDetails) {
        List<BriefCardDTO> dispatchCards = new ArrayList<>();

        //step 1 : 过滤掉在店铺黑名单中的订单
        List<StorePayBO> payableStorePays = getCardPayableStorePays(storePayDetails);

        //step 2 : 礼卡排序(按照权重、范围、余额、过期时间、发卡时间排序)
        List<BriefCardDTO> allCards = new ArrayList<>(cards);
        Collections.sort(allCards, cardComparator);

        //step 2.2 过滤掉不能支付的礼卡
        List<BriefCardDTO> cardList = new ArrayList<>();
        for (BriefCardDTO cardDTO : allCards) {
            if (canPay(cardDTO, payableStorePays)) {
                cardList.add(cardDTO);
            }
        }

        //step 3 : 订单支持礼卡数量汇总并排序
        sortByCardMatchCount(payableStorePays, cards);

        //step 4 : 记录礼卡可分摊金额
        LinkedHashMap<String, BigDecimal> cardRestMap = new LinkedHashMap<>();
        cardList.forEach(item -> {
            cardRestMap.put(item.getCardNo(), item.getAmount());
        });

        //step 5 : 执行分摊操作
        BigDecimal dispatchRest = dispatchLimit;
        for (BriefCardDTO briefCardDTO : cardList) {
            BigDecimal cardRest = cardRestMap.get(briefCardDTO.getCardNo());

            //执行单个礼卡对整个支付订单的分摊操作
            BigDecimal curLimit = MathUtil.min(dispatchRest, cardRest);
            BigDecimal cardDispatch = dispatchCard(curLimit, briefCardDTO, payableStorePays);

            //更新总的可分摊金额
            dispatchRest = dispatchRest.subtract(cardDispatch);

            //更新卡剩余可用金额
            cardRestMap.put(briefCardDTO.getCardNo(), cardRest.subtract(cardDispatch));

            if (MathUtil.greaterThanZero(cardDispatch)) {
                dispatchCards.add(briefCardDTO);
            }
        }
        return Pair.of(dispatchCards, dispatchLimit.subtract(dispatchRest));
    }

    private static BigDecimal dispatchCard(BigDecimal dispatchLimit, BriefCardDTO briefCardDTO, List<StorePayBO> payableStorePays) {
        BigDecimal dispatch = BigDecimal.ZERO;

        BigDecimal dispatchRest = dispatchLimit;
        storeDispatch:
        for (StorePayBO storePayBO : payableStorePays) {
            if (!canPay(briefCardDTO, storePayBO)) {
                continue storeDispatch;
            }
            //若店铺订单已经分摊完毕，则跳过此店铺
            if (storePayBO.isDispatchFinished() || MathUtil.sameValue(dispatchRest, BigDecimal.ZERO)) {
                continue storeDispatch;
            }

            goodsDispatch:
            for (GoodsPayBO goodsPay : storePayBO.getGoodsPayDetails()) {
                //此商品已经使用站内支付分摊完毕，跳过此商品
                if (!canPay(briefCardDTO, goodsPay) || goodsPay.isDispatchFinished() || MathUtil.sameValue(dispatchRest, BigDecimal.ZERO)) {
                    continue goodsDispatch;
                }

                //取礼卡剩余、商品剩余、用户提交礼卡分摊剩余中最小值进行分摊，防止计算超额
                BigDecimal curDispatch = MathUtil.min(dispatchRest, goodsPay.getUndispatchPrice());
                //记录单品的礼卡分摊记录
                goodsPay.putCardDetail(briefCardDTO.getCardNo(), curDispatch);
                //更新剩余可分摊额度
                dispatchRest = dispatchRest.subtract(curDispatch);
                //这张卡分摊掉的总额
                dispatch = dispatch.add(curDispatch);

                //更新单品分摊结果
                goodsPay.updatePrice();
            }

            //分摊运费
            BigDecimal freightDispatch = MathUtil.min(dispatchRest, storePayBO.getUndispatchDispatchFreight());
            if (MathUtil.greaterThanZero(freightDispatch)) {
                dispatchRest = dispatchRest.subtract(freightDispatch);
                dispatch = dispatch.add(freightDispatch);
                storePayBO.getCardsFreightDetail().put(briefCardDTO.getCardNo(), freightDispatch);
            }

            //更新店铺分摊结果
            storePayBO.updatePrice();
        }
        return dispatch;
    }

    /**
     * 将店铺订单和其下的商品按照匹配的礼卡数量升序排序
     *
     * @param payableStorePays
     * @param cardList
     */
    private static void sortByCardMatchCount(List<StorePayBO> payableStorePays, List<BriefCardDTO> cardList) {
        //将商品按照礼卡匹配数量升序排序
        for (StorePayBO storePayBO : payableStorePays) {
            for (GoodsPayBO goodsPayBO : storePayBO.getGoodsPayDetails()) {
                for (BriefCardDTO cardDTO : cardList) {
                    if (canPay(cardDTO, goodsPayBO)) {
                        goodsPayBO.matchCardCountIncrease();
                    }
                }
            }
            Collections.sort(storePayBO.getGoodsPayDetails(), pkgDispatchComparator);
        }

        //将订单按照支持礼卡数量升序排序，支持卡数少的订单先分摊，以此最大化礼卡分摊覆盖面
        for (StorePayBO storePayBO : payableStorePays) {
            for (BriefCardDTO cardDTO : cardList) {
                if (canPay(cardDTO, storePayBO)) {
                    storePayBO.matchCardCountIncrease();
                }
            }
        }
        Collections.sort(payableStorePays, pkgDispatchComparator);
    }

    /**
     * 计算礼卡能否支付此商品
     *
     * @param cardDTO  礼卡
     * @param goodsPay 商品支付信息
     * @return
     */
    private static boolean canPay(BriefCardDTO cardDTO, GoodsPayBO goodsPay) {
        Integer applyScope = cardDTO.getApplyScope().intValue();
        if (ApplyScopeEnum.APPLY_GOODS.getScopeId().equals(applyScope)) {
            List<Long> itemIds = cardDTO.getGoodsIds();
            //白名单
            if (CollectionUtils.isEmpty(itemIds) || !itemIds.contains(goodsPay.getItemId())) {
                log.warn("card {} with white-list {} dose not allow goods {} to pay by card", cardDTO.getCardNo(),
                        JSON.toJSONString(cardDTO.getGoodsIds()), goodsPay.getSkuId());
                return false;
            }
        }
        return true;
    }

    /**
     * 判断礼卡是否能够用于此店铺订单的支付
     *
     * @param cardDTO
     * @param storePay
     * @return
     */
    private static boolean canPay(BriefCardDTO cardDTO, StorePayBO storePay) {
        Long storeId = storePay.getStoreId();
        Integer applyScope = cardDTO.getApplyScope().intValue();
        if (ApplyScopeEnum.APPLY_INCLUDE.getScopeId().equals(applyScope)) {
            //白名单
            if (CollectionUtils.isEmpty(cardDTO.getStoreIds()) || !cardDTO.getStoreIds().contains(storeId)) {
                log.warn("card {} with white-list {} dose not allow store {} to pay by card", cardDTO.getCardNo(),
                        JSON.toJSONString(cardDTO.getStoreIds()), storeId);
                return false;
            }
        } else if (ApplyScopeEnum.APPLY_EXCLUDE.getScopeId().equals(applyScope)) {
            //黑名单
            if (CollectionUtils.isNotEmpty(cardDTO.getStoreIds()) && cardDTO.getStoreIds().contains(storeId)) {
                log.warn("card {} with black-list {} dose not allow store {} to pay by card", cardDTO.getCardNo(),
                        JSON.toJSONString(cardDTO.getStoreIds()), storeId);
                return false;
            }
        } else if (ApplyScopeEnum.APPLY_GOODS.getScopeId().equals(applyScope)) {
            List<Long> goodsIds = cardDTO.getGoodsIds();
            if (CollectionUtils.isEmpty(goodsIds)) {
                log.warn("card {} with empty goods white-list", cardDTO.getCardNo());
                return false;
            }
            Set<Long> goodsIdSet = new HashSet<>(goodsIds);
            for (GoodsPayBO goodsPay : storePay.getGoodsPayDetails()) {
                if (goodsIdSet.contains(goodsPay.getItemId())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean canPay(BriefCardDTO cardDTO, List<StorePayBO> storePays) {
        boolean canPay = false;
        for (StorePayBO payBO : storePays) {
            if (canPay(cardDTO, payBO)) {
                canPay = true;
                break;
            }
        }
        return canPay;
    }

    /**
     * 查询礼卡可以支付订单范围
     *
     * @param srcStorePays
     * @return
     */
    private static List<StorePayBO> getCardPayableStorePays(List<StorePayBO> srcStorePays) {
        PayConfiguration payConf = PayConfigContext.get();
        Set<Long> storeBlackList = payConf.getCardStoreBlackList();

        //店铺礼卡支付黑名单为空，则允许所有店铺使用礼卡进行支付
        if (CollectionUtils.isEmpty(storeBlackList)) {
            log.info("card store black list is empty");
            return srcStorePays;
        }

        //校验店铺礼卡支付黑名单
        List<StorePayBO> supportPays = new ArrayList<>();
        Iterator<StorePayBO> storePayIter = srcStorePays.iterator();
        while (storePayIter.hasNext()) {
            StorePayBO storePay = storePayIter.next();
            //店铺ID在礼卡消费黑名单中,则将此订单从分摊订单中移除
            if (storeBlackList.contains(storePay.getStoreId())) {
                log.warn("{} is in card pay support black list", storePay.getStoreId());
                continue;
            }
            supportPays.add(storePay);
        }
        return supportPays;
    }

}