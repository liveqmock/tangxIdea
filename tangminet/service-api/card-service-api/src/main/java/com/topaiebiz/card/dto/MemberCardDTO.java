package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 会员有效礼卡信息
 * @author: Jeff Chen
 * @date: created in 上午9:42 2018/1/9
 */
public class MemberCardDTO implements Serializable {
    private static final long serialVersionUID = 5320510339131934542L;

    /**
     * 有效卡张数
     */
    private Integer totalCardNum;
    /**
     * 有效卡总余额
     */
    private BigDecimal totalCardAmount;

    /**
     * 有效卡列表
     */
    private List<BriefCardDTO> briefCardDTOList;

    public MemberCardDTO() {
        this.totalCardNum = 0;
        this.totalCardAmount = BigDecimal.ZERO;
        this.briefCardDTOList = new ArrayList<>(0);
    }

    public Integer getTotalCardNum() {
        return totalCardNum;
    }

    public void setTotalCardNum(Integer totalCardNum) {
        this.totalCardNum = totalCardNum;
    }

    public BigDecimal getTotalCardAmount() {
        return totalCardAmount;
    }

    public void setTotalCardAmount(BigDecimal totalCardAmount) {
        this.totalCardAmount = totalCardAmount;
    }

    public List<BriefCardDTO> getBriefCardDTOList() {
        return briefCardDTOList;
    }

    public void setBriefCardDTOList(List<BriefCardDTO> briefCardDTOList) {
        this.briefCardDTOList = briefCardDTOList;
    }

    @Override
    public String toString() {
        return "MemberCardDTO{" +
                "totalCardNum=" + totalCardNum +
                ", totalCardAmount=" + totalCardAmount +
                ", briefCardDTOList=" + briefCardDTOList +
                '}';
    }
}
