package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 卡密
 * @author: Jeff Chen
 * @date: created in 下午8:53 2018/1/16
 */
@Data
public class GiftcardExportVO implements Serializable {

    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 密码
     */
    private String password;
}
