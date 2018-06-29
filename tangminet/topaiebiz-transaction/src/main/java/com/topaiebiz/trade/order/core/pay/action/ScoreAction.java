package com.topaiebiz.trade.order.core.pay.action;

import com.topaiebiz.member.constants.AssetOperateType;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.MemberContext;
import com.topaiebiz.trade.order.facade.ScoreServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-20 17:12
 */
@Component
public class ScoreAction implements PayAction {

    @Autowired
    private ScoreServiceFacade scoreServiceFacade;

    private boolean hasNoScoreAndBalance(PayRequest payRequest) {
        return MathUtil.sameValue(payRequest.getBalance(), BigDecimal.ZERO)
                && MathUtil.sameValue(payRequest.getScore(), BigDecimal.ZERO);
    }

    @Override
    public boolean action(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        if (hasNoScoreAndBalance(payRequest)) {
            //未使用积分和余额进行支付
            return true;
        }

        MemberDto memberDto = MemberContext.get();
        AssetChangeDto param = buildPayParam(memberDto, AssetOperateType.BUY_CONSUME, payRequest);

        return scoreServiceFacade.useAccountAssets(param);
    }

    /**
     * 构建积分余额支付参数
     *
     * @param memberDto
     * @param operateType
     * @param payRequest
     * @return
     */
    private AssetChangeDto buildPayParam(MemberDto memberDto, AssetOperateType operateType, PayRequest payRequest) {
        AssetChangeDto parm = new AssetChangeDto();
        parm.setMemberId(memberDto.getId());
        parm.setTelephone(memberDto.getTelephone());
        parm.setUserName(memberDto.getUserName());

        parm.setBalance(payRequest.getBalance());
        parm.setPoint(MathUtil.getScoreNum(payRequest.getScore()));
        parm.setOperateSn(payRequest.getPayId().toString());
        parm.setOperateType(operateType);
        return parm;
    }

    @Override
    public void rollback(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        if (hasNoScoreAndBalance(payRequest)) {
            return;
        }
        MemberDto memberDto = MemberContext.get();
        AssetChangeDto param = buildPayParam(memberDto, AssetOperateType.REFUND, payRequest);
        scoreServiceFacade.rollbackAccountAssets(param);
    }
}