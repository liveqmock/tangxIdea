package com.topaiebiz.trade.order.dto.pay;

import com.google.common.collect.Lists;
import com.topaiebiz.card.dto.BriefCardDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/***
 * 用户资产
 * @author yfeng
 * @date 2018-01-17 22:19
 */
@Data
public class MemberAssetDTO {
    /***    可用余额   ***/
    private BigDecimal balance = BigDecimal.ZERO;

    /***    可用积分   ***/
    private BigDecimal score = BigDecimal.ZERO;
    /***    积分数     ***/
    private Integer scoreNum;

    /***    可用礼卡金额   ***/
    private BigDecimal cardAmount = BigDecimal.ZERO;

    /***    可用礼卡张数   ***/
    private Integer cardNum = 0;
    private List<BriefCardDTO> cards = Lists.newArrayList();
    /**
     * 是否有限制范围使用的礼卡(圈店铺卡或圈商品卡)
     */
    private boolean hasLimitCards;

    /**
     * 账户资产
     */
    private BigDecimal accountScore = BigDecimal.ZERO;
    private Integer accountScoreNum = 0;
    private BigDecimal accountBalance = BigDecimal.ZERO;
    private BigDecimal accountCard = BigDecimal.ZERO;
}