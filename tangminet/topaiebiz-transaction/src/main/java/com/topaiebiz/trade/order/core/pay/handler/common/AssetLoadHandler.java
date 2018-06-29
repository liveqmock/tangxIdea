package com.topaiebiz.trade.order.core.pay.handler.common;

import com.topaiebiz.member.dto.point.MemberAssetDto;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.handler.PayContextHandler;
import com.topaiebiz.trade.order.core.pay.util.StorePayUtil;
import com.topaiebiz.trade.order.dto.pay.MemberAssetDTO;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.facade.ScoreServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/***
 * 加载用户的积分和余额资产信息
 * @author yfeng
 * @date 2018-01-19 14:44
 */
@Component("assetLoadHandler")
public class AssetLoadHandler implements PayContextHandler {

    @Autowired
    private ScoreServiceFacade scoreServiceFacade;

    @Override
    public void prepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        PaySummaryDTO paySummary = PaySummaryContext.get();
        paySummary.initMemberAsset();

        MemberAssetDto assetDto = scoreServiceFacade.getMemberAsset(buyer.getMemberId());
        if (assetDto == null) {
            return;
        }

        //step 2 : 计算本单可用余额
        BigDecimal curUsefulBalance = BigDecimal.ZERO;
        if (assetDto.getBalance() != null) {
            curUsefulBalance = MathUtil.min(paySummary.getMaxPkgPay(), assetDto.getBalance());
        }
        MemberAssetDTO memberAsset = paySummary.getMemberAsset();
        memberAsset.setBalance(curUsefulBalance);

        //step 3 : 计算本单可用积分
        //资产中的积分
        BigDecimal assetScore = MathUtil.getScoreAmount(assetDto.getPoint());
        //根据商品的兑换比例计算本单最高可用积分金额
        BigDecimal goodsScoreLimit = StorePayUtil.getScoreSupportAmount(paramContext.getStorePayDetails());
        //本单最终可用积分为，min(商品限制,用户资产)
        BigDecimal curUsefulScore = MathUtil.min(goodsScoreLimit, paySummary.getMaxPkgPay(), assetScore);

        memberAsset.setScore(curUsefulScore);
        memberAsset.setScoreNum(MathUtil.getScoreNum(curUsefulScore));

        //赋值账户资产
        memberAsset.setAccountBalance(assetDto.getBalance());
        memberAsset.setAccountScoreNum(assetDto.getPoint());
        memberAsset.setAccountScore(MathUtil.getScoreAmount(assetDto.getPoint()));
    }
}