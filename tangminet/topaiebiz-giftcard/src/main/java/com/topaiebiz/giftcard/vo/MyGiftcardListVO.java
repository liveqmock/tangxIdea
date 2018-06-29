package com.topaiebiz.giftcard.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午1:37 2018/1/24
 */
public class MyGiftcardListVO implements Serializable {

    /**
     * 可以卡列表
     */
    private List<MyGiftcardVO> validCardList;

    /**
     * 不可用卡列表
     */
    private List<MyGiftcardVO> invalidCardList;

    public List<MyGiftcardVO> getValidCardList() {
        return validCardList;
    }

    public void setValidCardList(List<MyGiftcardVO> validCardList) {
        this.validCardList = validCardList;
    }

    public List<MyGiftcardVO> getInvalidCardList() {
        return invalidCardList;
    }

    public void setInvalidCardList(List<MyGiftcardVO> invalidCardList) {
        this.invalidCardList = invalidCardList;
    }
}
