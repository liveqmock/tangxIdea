package com.topaiebiz.trade.order.core.cancel.action;

import com.topaiebiz.member.constants.AssetOperateType;
import com.topaiebiz.member.dto.point.AssetChangeDto;
import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.facade.ScoreServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-21 19:04
 */
@Slf4j
@Component("scoreBackAction")
public class ScoreBackAction implements CancelAction {

    @Autowired
    private ScoreServiceFacade scoreServiceFacade;

    @Override
    public boolean action(BuyerBO buyerBO, CancelParamContext context) {
        OrderPayEntity payEntity = context.getPayEntity();
        if (MathUtil.greaterThanZero(payEntity.getScorePrice())
                || MathUtil.greaterThanZero(payEntity.getBalance())) {
            AssetChangeDto assetChangeDto = buildParam(buyerBO, payEntity);
            return scoreServiceFacade.rollbackAccountAssets(assetChangeDto);
        }
        log.warn("palyEntity {} did not use score and balance");
        return true;
    }

    private AssetChangeDto buildParam(BuyerBO buyerBO, OrderPayEntity payEntity) {
        AssetChangeDto param = new AssetChangeDto();
        param.setMemberId(buyerBO.getMemberId());
        param.setTelephone(buyerBO.getMobile());
        param.setUserName(buyerBO.getMemberName());

        if (MathUtil.greaterThanZero(payEntity.getBalance())){
            param.setBalance(payEntity.getBalance());
        }
        if (MathUtil.greaterThanZero(payEntity.getScorePrice())){
            param.setPoint(payEntity.getScoreNum().intValue());
        }
        param.setOperateSn(payEntity.getId().toString());
        param.setOperateType(AssetOperateType.REFUND);
        return param;
    }
}